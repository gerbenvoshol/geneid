#!/usr/local/bin/perl -w

##################################################################
#
# MatScan Moby Service Client
#
##################################################################

use strict;
use Data::Dumper;

# be prepare for command-line options/arguments
use Getopt::Std;

# BioMoby and SOAP libraries

use MOBY::Client::Central;
use MOBY::Client::Service;
use SOAP::Lite;
# use SOAP::Lite + 'trace';

# Bioperl Libraries

use Bio::SeqIO;

# Benchmark Module
use Benchmark;
##################################################################

sub help {
return <<"END_HELP";
Description: Execute MetaAlignment Moby services available from genome.imim.es
Usage:

    MetaAlignment_Moby_Service.pl [-h] -x {Moby Central} -s {Service Name} -f {maps file}
	-h help
	-x MOBY Central: Chirimoyo, Mobydev, Inab or BioMoby
		<1> or Chirimoyo
		<2> or Mobydev
		<3> or Inab
		<4> or BioMoby
	-s Service Name
	-i Maps input file, in GFF format - optional
	
Examples using some combinations:
	perl MetaAlignment_Moby_Service.pl -x 2 -s runMultiPairwiseMetaAlignment

END_HELP

}

BEGIN {
	
	# Determines the options with values from program
	use vars qw/$opt_h $opt_x $opt_s $opt_f/;
	   
	# these are switches taking an argument (a value)
	my $switches = 'hxsf';
	   
	# Get the switches
	getopt($switches);
	
	# If the user does not write nothing, skip to help
	if (defined($opt_h) || !defined($opt_x) || !defined($opt_s)){
		print help;
		exit 0;
	}
	
}

my $t1 = Benchmark->new ();

my $_debug = 0;

##################################################################
#
# Setup sequence input data and moby parameters
#
##################################################################

my $serviceName = $opt_s;
my $articleName = "maps";
$::authURI = 'genome.imim.es';

my $maps_file = "/home/ug/arnau/data/promoterExtraction/mut1_downreg.1000.intergenic.matscan.gff.xml";

# Parameters

my $alpha_penalty  = "0.5";
my $lambda_penalty = "0.1";
my $mu_penalty     = "0.1";

##################################################################
#
# Setup Moby configuration parameters
#
##################################################################

##############################################
#
# ASSIGN THE MOBY URI AND MOBY SERVER
#
##############################################

my $URL   = $ENV{MOBY_SERVER}?$ENV{MOBY_SERVER}:'http://chirimoyo.ac.uma.es/cgi-bin/MOBY-Central.pl';
my $URI   = $ENV{MOBY_URI}?$ENV{MOBY_URI}:'http://chirimoyo.ac.uma.es/MOBY/Central';
my $PROXY = $ENV{MOBY_PROXY}?$ENV{MOBY_PROXY}:'No Proxy Server';

if (defined($opt_x)) {

	# Delete spaces
	$opt_x =~ s/\s//g;

	# Assign the MOBY Server and MOBY URI
	if (($opt_x == 1) || ($opt_x eq 'Chirimoyo')) {
	    
	    # export MOBY_URI
	    # export MOBY_SERVER
	    $URL   = $ENV{MOBY_SERVER}?$ENV{MOBY_SERVER}:'http://chirimoyo.ac.uma.es/cgi-bin/MOBY-Central.pl';
	    $URI   = $ENV{MOBY_URI}?$ENV{MOBY_URI}:'http://chirimoyo.ac.uma.es/MOBY/Central';
	    $PROXY = $ENV{MOBY_PROXY}?$ENV{MOBY_PROXY}:'No Proxy Server';
	
	}elsif (($opt_x == 2) || ($opt_x eq 'Mobydev')) {
	
		# export MOBY_URI
		# export MOBY_SERVER
		$URL   = $ENV{MOBY_SERVER}?$ENV{MOBY_SERVER}:'http://moby-dev.inab.org/cgi-bin/MOBY-Central.pl';
		$URI   = $ENV{MOBY_URI}?$ENV{MOBY_URI}:'http://moby-dev.inab.org/MOBY/Central';
		$PROXY = $ENV{MOBY_PROXY}?$ENV{MOBY_PROXY}:'No Proxy Server';

	}elsif (($opt_x == 3) || ($opt_x eq 'Inab')) {

		# export MOBY_URI
		# export MOBY_SERVER
		$URL   = $ENV{MOBY_SERVER}?$ENV{MOBY_SERVER}:'http://www.inab.org/cgi-bin/MOBY-Central.pl';
		$URI   = $ENV{MOBY_URI}?$ENV{MOBY_URI}:'http://www.inab.org/MOBY/Central';
		$PROXY = $ENV{MOBY_PROXY}?$ENV{MOBY_PROXY}:'No Proxy Server';
	
	}elsif (($opt_x == 4) || ($opt_x eq 'BioMoby')) {

		# export MOBY_URI
		# export MOBY_SERVER
		$URL   = $ENV{MOBY_SERVER}?$ENV{MOBY_SERVER}:'http://mobycentral.icapture.ubc.ca/cgi-bin/MOBY05/mobycentral.pl';
		$URI   = $ENV{MOBY_URI}?$ENV{MOBY_URI}:'http://mobycentral.icapture.ubc.ca/MOBY/Central';
		$PROXY = $ENV{MOBY_PROXY}?$ENV{MOBY_PROXY}:'No Proxy Server';

	}else {
		print help;
		exit 0;
	}

}else {
	print help;
	exit 0;
}

##################################################################
#
# Moby Server Instanciation
#
##################################################################

if ($_debug) {
    print STDERR "Instanciating Central object...\n";
}

my $C = MOBY::Client::Central->new(
				   Registries => {mobycentral => {URL => $URL,URI => $URI}}
				   );

if (defined $C) {
    if ($_debug) {
	print STDERR "Instanciation done.\n";
	print STDERR "TESTING MOBY CLIENT with\n\tURL: $URL\n\tURI: $URI\n\tProxy: $PROXY\n\n";
    }
}
else {
    print STDERR "Error, Central could not be instanciated!\n";
    exit 0;
}

##################################################################
#
# Moby Service instantiation
#
##################################################################

if ($_debug) {
    print STDERR "finding service, $serviceName...\n";
}

my ($service_instances, $reg) = $C->findService (
						 serviceName  => "$serviceName",
						 authURI      => $::authURI
						 );

if ($_debug) {
    print STDERR "Service instances references: " . @$service_instances . "\n";
}

if (@$service_instances == 0) {
    print STDERR "Error, can't find any service called $serviceName!\n";
    exit 0;
}

my $service_instance = $service_instances->[0];

my $wsdl = $C->retrieveService($service_instance);

if ($_debug) {
    print STDERR "wsdl: $wsdl\n";
}

if (!$wsdl || ($wsdl !~ /\<definitions/)){
    print "test \t\t[FAIL]\tWSDL was not retrieved\n\n";
}

my $Service = MOBY::Client::Service->new(service => $wsdl);

##################################################################
#
# Parse the xml input objects from the input file
#
##################################################################

open FILE, "$maps_file" or die "can't open maps file!\n";
my $maps_xml = [];
my $map_xml  = "";

my $maximum_pairs = 50;
my $index = 0;

while (<FILE>) {

    if ($index >= $maximum_pairs) {
	last;
    }

    my $line = $_;
    if ($line =~ /<moby:Simple>/) {
	my $parsing = 1;
	while ($parsing && ($line = <FILE>)) {
	    if (not ($line =~ /<\/moby:Simple>/)) {
		$map_xml .= $line;
	    }
	    else {
		
		# print STDERR "map_xml: $map_xml\n";
		
		push (@$maps_xml, $map_xml);
		$map_xml = "";
		$parsing = 0;
		$index++;
	    }
	}
    }
}

close FILE;

if ($_debug) {
    print STDERR "got " . @$maps_xml . " maps\n";
}

#
# Parameters (secondary articles)
#

# Alpha penalty parameter

my $alpha_xml = <<PRT;
<Value>$alpha_penalty</Value>
PRT

# Lambda penalty parameter
    
my $lambda_xml = <<PRT;
<Value>$lambda_penalty</Value>
PRT

# Mu penalty parameter

my $mu_xml = <<PRT;
<Value>$mu_penalty</Value>
PRT
    
##################################################################
#
# Service execution
#
##################################################################

my $result = $Service->execute(XMLinputlist => [
						["$articleName", $maps_xml, 'alpha penalty', $alpha_xml, 'lambda penalty' => $lambda_xml, 'mu penalty' => $mu_xml]
					       ]);

##################################################################
#
# Result processing
#
##################################################################

print STDERR "result\n";
print $result;
print STDERR "\n";

my $t2 = Benchmark->new ();
print STDERR "\nTotal : ", timestr (timediff ($t2, $t1)), "\n";