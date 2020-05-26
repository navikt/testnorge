# Testnorge Ereg-mapper

The application provides an interface for creating flatmapped files which simulates the records which is fetched from 
BREG. It also provides an interface for submitting these files to the batch executor for EREG. 

## Deployment
The repository is linked to NAVIKT's circle pipeline and is automatically build on the master branch and on pull requests
to master.

## About the automatic generation of data

The name and næringskode attributes are generated through a csv file containing the records. This file is
created from the the values in _Kodeverk_ and the definition of [NACE Næringskoder](https://www.brreg.no/bedrift/naeringskoder/). _Kodeverk_ takes 
presidence and contains the actual values that can be inserted into EREG, but it does not a good description of the 
name and purpose so the values from BREG is used.

A simple script is used to generate this file and the copied in to the repository. This was a simpler 
solution than including auto-generated classes from a WSDL to parse the request to _Kodeverk_. 

``` python
import csv

import xml.etree.ElementTree as ET


def run():
    root = ET.parse('kodeverk.xml').getroot()
    body = root.find('{http://schemas.xmlsoap.org/soap/envelope/}Body')
    op = body.find('{http://nav.no/tjeneste/virksomhet/kodeverk/v2/}hentKodeverkResponse')
    response = op.find('response')
    kodeverk = response.find('kodeverk')
    codes = kodeverk.findall('kode')

    found = []

    for code in codes:
        valid_period = code.find('gyldighetsperiode')
        found.append((code.find('navn').text, valid_period.find('fom').text, valid_period.find('tom').text))

    csv_codes = []
    in_both = []

    csv_writer = csv.writer(open("ferdigstilte_koder.csv", "w"), delimiter=";")

    csv_reader = csv.reader(open('koder.csv'), delimiter=";", skipinitialspace=True)
    for (i, l) in enumerate(csv_reader):
        if i == 0:
            csv_writer.writerow(l)
            continue
        for f in found:
            if l[0].strip() == f[0].strip():
                if len(l) < 7:
                    l.append(f[1].strip())
                    l.append(f[2].strip())
                else:
                    l[6] = f[1].strip()
                    l[7] = f[2].strip()
                in_both.append(l)
                csv_writer.writerow(l)

        csv_codes.append(l)

    for i in in_both:
        occourences = 0
        for x in in_both:
            if i == x:
                occourences += 1
        if occourences == 2:
            print(i)

    print(len(in_both))
    print(len(csv_codes) - len(in_both))


if __name__ == '__main__':
    run()
```

This script concatinates the two csv files, but keep only the values present in the `kodeverk.csv` file.

### Updating the næringskode file
Just run the above script with the two files present in the same directory and copy in the outputted `ferdigstilte_koder.csv`
into `testnorge-ereg-mapper/ereg-mapper-core/src/main/resources/naeringskoder.csv` which contain the final values.

### Generation of orgnr
A orgnr generator can be found in ognr_generator folder. This requires the `requests` library. It checks both the synthetic environment and the production like
environment for existsing orgnr.