import { Kategorier, SubKategorier } from '../Categories'
import { Attributt, InputType, DataSource, AttributtType } from '../Types'
import DateValidation from '~/components/fields/Datepicker/DateValidation'

import * as yup from 'yup'

const AttributtListe: Attributt[] = [
	// Gateadresse
	{
		hovedKategori: Kategorier.Adresser,
		subKategori: SubKategorier.Boadresse,
		id: 'boadresse',
		path: 'boadresse',
		label: 'Har gateadresse',
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
		apiKodeverkId: 'Postnummer',
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
		attributtType: AttributtType.SelectAndEdit
	},
	// Matrikkeladresse
	{
		hovedKategori: Kategorier.Adresser,
		subKategori: SubKategorier.Boadresse,
		id: 'matrikkeladresse',
		label: 'Har matrikkeladresse',
		dataSource: DataSource.TPSF,
		validation: yup.object(),
		attributtType: AttributtType.SelectAndEdit,
		dependentBy: 'boadresse_flyttedato',
		items: [
			{
				hovedKategori: Kategorier.Adresser,
				subKategori: SubKategorier.Boadresse,
				id: 'mellomnavn',
				label: 'Stedsnavn',
				dataSource: DataSource.TPSF,
				inputType: InputType.Text,
				attributtType: AttributtType.SelectAndEdit
			},
			{
				hovedKategori: Kategorier.Adresser,
				subKategori: SubKategorier.Boadresse,
				id: 'gardsnr',
				label: 'Gårdsnummer',
				dataSource: DataSource.TPSF,
				inputType: InputType.Text,
				validation: yup
					.string()
					.max(4, 'For mange tegn')
					.required('Vennligst fyll ut'),
				attributtType: AttributtType.SelectAndEdit
			},
			{
				hovedKategori: Kategorier.Adresser,
				subKategori: SubKategorier.Boadresse,
				id: 'bruksnr',
				label: 'Bruksnummer',
				dataSource: DataSource.TPSF,
				inputType: InputType.Text,
				validation: yup
					.string()
					.max(4, 'For mange tegn')
					.required('Vennligst fyll ut'),
				attributtType: AttributtType.SelectAndEdit
			},
			{
				hovedKategori: Kategorier.Adresser,
				subKategori: SubKategorier.Boadresse,
				id: 'festenr',
				label: 'Festenummer',
				dataSource: DataSource.TPSF,
				inputType: InputType.Text,
				validation: yup.string().max(4, 'For mange tegn'),
				attributtType: AttributtType.SelectAndEdit
			},
			{
				hovedKategori: Kategorier.Adresser,
				subKategori: SubKategorier.Boadresse,
				id: 'undernr',
				label: 'Undernummer',
				dataSource: DataSource.TPSF,
				inputType: InputType.Text,
				validation: yup.string().max(3, 'For mange tegn'),
				attributtType: AttributtType.SelectAndEdit
			},
			{
				hovedKategori: Kategorier.Adresser,
				subKategori: SubKategorier.Boadresse,
				id: 'postnr',
				label: 'Postnummer',
				dataSource: DataSource.TPSF,
				inputType: InputType.Select,
				validation: yup.string().required('Vennligst velg'),
				apiKodeverkId: 'Postnummer',
				apiKodeverkShowValueInLabel: true,
				attributtType: AttributtType.SelectAndEdit
			},
			{
				hovedKategori: Kategorier.Adresser,
				subKategori: SubKategorier.Boadresse,
				id: 'kommunenr',
				label: 'Kommunenummer',
				dataSource: DataSource.TPSF,
				inputType: InputType.Select,
				validation: yup.string().required('Vennligst velg'),
				apiKodeverkId: 'Kommuner',
				apiKodeverkShowValueInLabel: true,
				attributtType: AttributtType.SelectAndEdit
			}
		]
	},
	// Flyttedato
	{
		hovedKategori: Kategorier.Adresser,
		subKategori: SubKategorier.Boadresse,
		id: 'boadresse_flyttedato',
		path: 'boadresse.flyttedato',
		label: 'Flyttedato',
		dataSource: DataSource.TPSF,
		inputType: InputType.Date,
		validation: DateValidation(),
		attributtType: AttributtType.SelectAndEdit,
		dependentOn: 'boadresse',
		defaultValue: new Date()
	},
	// Postadresse
	{
		hovedKategori: Kategorier.Adresser,
		subKategori: SubKategorier.Postadresse,
		id: 'postadresse',
		path: 'postadresse',
		label: 'Har postadresse',
		dataSource: DataSource.TPSF,
		validation: yup.object(),
		attributtType: AttributtType.SelectOnly
	},
	{
		hovedKategori: Kategorier.Adresser,
		subKategori: SubKategorier.Postadresse,
		id: 'postLand',
		path: 'postadresse.postLand',
		parent: 'postadresse',
		label: 'Land',
		dataSource: DataSource.TPSF,
		inputType: InputType.Select,
		apiKodeverkId: 'StatsborgerskapFreg',
		validation: yup.string(),
		attributtType: AttributtType.SelectOnly
	},
	{
		hovedKategori: Kategorier.Adresser,
		subKategori: SubKategorier.Postadresse,
		id: 'postLinje1',
		path: 'postadresse.postLinje1',
		parent: 'postadresse',
		label: 'Adresselinje 1',
		dataSource: DataSource.TPSF,
		inputType: InputType.Text,
		validation: yup.string().required('Vennligst fyll ut'),
		attributtType: AttributtType.SelectOnly
	},
	{
		hovedKategori: Kategorier.Adresser,
		subKategori: SubKategorier.Postadresse,
		id: 'postLinje2',
		path: 'postadresse.postLinje2',
		parent: 'postadresse',
		label: 'Adresselinje 2',
		dataSource: DataSource.TPSF,
		inputType: InputType.Text,
		attributtType: AttributtType.SelectOnly
	},
	{
		hovedKategori: Kategorier.Adresser,
		subKategori: SubKategorier.Postadresse,
		id: 'postLinje3',
		path: 'postadresse.postLinje3',
		parent: 'postadresse',
		label: 'Adresselinje 3',
		dataSource: DataSource.TPSF,
		inputType: InputType.Text,
		attributtType: AttributtType.SelectOnly
	}

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
