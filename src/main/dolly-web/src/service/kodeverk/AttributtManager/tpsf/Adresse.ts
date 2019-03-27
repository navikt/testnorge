import { Kategorier, SubKategorier } from '../Categories'
import { Attributt, InputType, DataSource, AttributtType } from '../Types'
import DateValidation from '~/components/fields/Datepicker/DateValidation'

import * as yup from 'yup'

const AttributtListe: Attributt[] = [
	{
		hovedKategori: Kategorier.Adresser,
		subKategori: SubKategorier.Boadresse,
		id: 'boadresse',
		path: 'boadresse',
		label: 'Har adresse',
		dataSource: DataSource.TPSF,
		validation: yup.object(),
		attributtType: AttributtType.SelectAndEdit,
		dependentBy: 'boadresse_flyttedato'
	},
	{
		hovedKategori: Kategorier.Adresser,
		subKategori: SubKategorier.Boadresse,
		id: 'boadresse_gateadresse',
		path: 'boadresse.gateadresse',
		parent: 'boadresse',
		label: 'Gatenavn',
		dataSource: DataSource.TPSF,
		inputType: InputType.Text,
		// validation: yup.string().required('Skriv minst tre bokstaver eller hent gyldig adresse'),
		attributtType: AttributtType.SelectAndEdit
	},
	{
		hovedKategori: Kategorier.Adresser,
		subKategori: SubKategorier.Boadresse,
		id: 'boadresse_husnummer',
		path: 'boadresse.husnummer',
		parent: 'boadresse',
		label: 'Husnummer',
		dataSource: DataSource.TPSF,
		inputType: InputType.Text,
		// validation: yup.string().required('Vennligst fyll ut eller hent gyldig adresse'),
		attributtType: AttributtType.SelectAndEdit
	},
	{
		hovedKategori: Kategorier.Adresser,
		subKategori: SubKategorier.Boadresse,
		id: 'boadresse_postnr',
		path: 'boadresse.postnr',
		parent: 'boadresse',
		label: 'Postnummer',
		dataSource: DataSource.TPSF,
		inputType: InputType.Select,
		// validation: yup.string().required('Vennligst fyll ut eller hent gyldig adresse'),
		attributtType: AttributtType.SelectAndEdit
	},
	{
		hovedKategori: Kategorier.Adresser,
		subKategori: SubKategorier.Boadresse,
		id: 'boadresse_kommunenr',
		path: 'boadresse.kommunenr',
		parent: 'boadresse',
		label: 'Kommunenummer',
		dataSource: DataSource.TPSF,
		inputType: InputType.Select,
		// validation: yup.string().required('Vennligst fyll ut eller hent gyldig adresse'),
		attributtType: AttributtType.SelectAndEdit
	},
	{
		hovedKategori: Kategorier.Adresser,
		subKategori: SubKategorier.Boadresse,
		id: 'boadresse_flyttedato',
		path: 'boadresse.flyttedato',
		label: 'Flyttedato',
		dataSource: DataSource.TPSF,
		inputType: InputType.Date,
		validation: DateValidation,
		attributtType: AttributtType.SelectAndEdit,
		dependentOn: 'boadresse'
	}
	// {
	// 	hovedKategori: Kategorier.Adresser,
	// 	subKategori: SubKategorier.Boadresse,
	// 	id: 'boadresse_gyldigadresse',
	// 	path: 'boadresse.gyldigadresse',
	// 	parent: 'boadresse',
	// 	label: 'Gyldigadresse',
	// 	dataSource: DataSource.TPSF,
	// 	inputType: InputType.Select,
	// 	validation: yup.string().required('Vennligst velg en gyldig adresse'),
	// 	attributtType: AttributtType.SelectAndEdit
	// }

	// {
	// 	hovedKategori: Kategorier.Adresser,
	// 	subKategori: SubKategorier.Boadresse,
	// 	id: 'geoTilknytning',
	// 	label: 'Geografisk tilknytning',
	// 	dataSource: DataSource.TPSF,
	// 	inputType: InputType.Text,
	// 	validation: yup.string()
	// },
	// {
	// 	hovedKategori: Kategorier.Adresser,
	// 	subKategori: SubKategorier.Boadresse,
	// 	id: 'matrikkeladresse',
	// 	label: 'Matrikkeladresse',
	// 	dataSource: DataSource.TPSF,
	// 	inputType: InputType.Text,
	// 	validation: yup.string()
	// },
	// {
	// 	hovedKategori: Kategorier.Adresser,
	// 	subKategori: SubKategorier.Boadresse,
	// 	id: 'gardsnummer',
	// 	label: 'Gårdsnummer',
	// 	dataSource: DataSource.TPSF,
	// 	inputType: InputType.Text,
	// 	validation: yup.string()
	// },
	// {
	// 	hovedKategori: Kategorier.Adresser,
	// 	subKategori: SubKategorier.Boadresse,
	// 	id: 'bruksnummer',
	// 	label: 'Bruksnummer',
	// 	dataSource: DataSource.TPSF,
	// 	inputType: InputType.Text,
	// 	validation: yup.string()
	// }
	// {
	// 	hovedKategori: Kategorier.Adresser,
	// 	subKategori: SubKategorier.PostadrInnland,
	// 	id: 'gatenavn',
	// 	label: 'Gatenavn',
	// 	dataSource: DataSource.TPSF,
	// 	inputType: InputType.Text,
	// 	validation: yup.string()
	// },
	// {
	// 	hovedKategori: Kategorier.Adresser,
	// 	subKategori: SubKategorier.PostadrInnland,
	// 	id: 'husnummer',
	// 	label: 'Husnummer',
	// 	dataSource: DataSource.TPSF,
	// 	inputType: InputType.Text,
	// 	validation: yup.string()
	// },
	// {
	// 	hovedKategori: Kategorier.Adresser,
	// 	subKategori: SubKategorier.PostadrInnland,
	// 	id: 'postnummer',
	// 	label: 'Postnummer',
	// 	dataSource: DataSource.TPSF,
	// 	inputType: InputType.Text,
	// 	validation: yup.string()
	// },
	// {
	// 	hovedKategori: Kategorier.Adresser,
	// 	subKategori: SubKategorier.PostadrInnland,
	// 	id: 'geoTilknytning',
	// 	label: 'Geografisk tilknytning',
	// 	dataSource: DataSource.TPSF,
	// 	inputType: InputType.Text,
	// 	validation: yup.string()
	// },
	// {
	// 	hovedKategori: Kategorier.Adresser,
	// 	subKategori: SubKategorier.PostadrInnland,
	// 	id: 'flyttedato',
	// 	label: 'Flyttedato',
	// 	dataSource: DataSource.TPSF,
	// 	inputType: InputType.Date,
	// 	validation: yup.date()
	// },
	// {
	// 	hovedKategori: Kategorier.Adresser,
	// 	subKategori: SubKategorier.PostadrUtland,
	// 	id: 'gatenavn',
	// 	label: 'Gatenavn',
	// 	dataSource: DataSource.TPSF,
	// 	inputType: InputType.Text,
	// 	validation: yup.string()
	// },
	// {
	// 	hovedKategori: Kategorier.Adresser,
	// 	subKategori: SubKategorier.PostadrUtland,
	// 	id: 'husnummer',
	// 	label: 'Husnummer',
	// 	dataSource: DataSource.TPSF,
	// 	inputType: InputType.Text,
	// 	validation: yup.string()
	// },
	// {
	// 	hovedKategori: Kategorier.Adresser,
	// 	subKategori: SubKategorier.PostadrUtland,
	// 	id: 'postnummer',
	// 	label: 'Postnummer',
	// 	dataSource: DataSource.TPSF,
	// 	inputType: InputType.Text,
	// 	validation: yup.string()
	// },
	// {
	// 	hovedKategori: Kategorier.Adresser,
	// 	subKategori: SubKategorier.PostadrUtland,
	// 	id: 'geoTilknytning',
	// 	label: 'Geografisk tilknytning',
	// 	dataSource: DataSource.TPSF,
	// 	inputType: InputType.Text,
	// 	validation: yup.string()
	// },
	// {
	// 	hovedKategori: Kategorier.Adresser,
	// 	subKategori: SubKategorier.PostadrUtland,
	// 	id: 'flyttedato',
	// 	label: 'Flyttedato',
	// 	dataSource: DataSource.TPSF,
	// 	inputType: InputType.Date,
	// 	validation: yup.date()
	// }
]

export default AttributtListe
