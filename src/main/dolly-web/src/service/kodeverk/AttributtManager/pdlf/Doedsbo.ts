import { Kategorier, SubKategorier, SubSubKategorier } from '../Categories'
import { Attributt, InputType, DataSource, AttributtType } from '../Types'
import SelectOptionsManager from '~/service/kodeverk/SelectOptionsManager/SelectOptionsManager'
import DateValidation from '~/components/fields/Datepicker/DateValidation'

import * as yup from 'yup'

const AttributtListe: Attributt[] = [
	{
		hovedKategori: Kategorier.KontaktInformasjonForDoedsbo,
		subKategori: SubKategorier.Doedsbo,
		id: 'kontaktinformasjonForDoedsbo',
		label: 'Har kontaktinformasjon for dødsbo',
		subGruppe: 'true',
		dataSource: DataSource.PDLF,
		attributtType: AttributtType.SelectAndEdit,
		validation: yup.object(),
		items: [
			{
				hovedKategori: Kategorier.KontaktInformasjonForDoedsbo,
				subKategori: SubKategorier.Adressat,
				id: 'adressatType',
				label: 'Adressattype',
				size: 'medium',
				path: 'adressat.type',
				subGruppe: 'Adressat',
				dataSource: DataSource.PDLF,
				validation: yup.string().required('Velg en type adressat'),
				inputType: InputType.Select,
				options: SelectOptionsManager('adressatType'),
				attributtType: AttributtType.SelectAndRead
			},
			{
				hovedKategori: Kategorier.KontaktInformasjonForDoedsbo,
				subKategori: SubKategorier.Adressat,
				id: 'idnummer',
<<<<<<< HEAD
				label: 'Fnr/dnr/bost',
=======
				label: 'Fnr/dnr/BOST',
>>>>>>> 722eec8db673a46c3b3c7342e28097a511e2576e
				path: 'adressat.idnummer',
				subGruppe: 'Adressat',
				dataSource: DataSource.PDLF,
				validation: yup
					.string()
					.required()
					.matches(/^[0-9]{11}$/, 'Id-nummer må være et tall med 11 sifre'),
				inputType: InputType.Text,
				onlyShowAfterSelectedValue: { attributtId: 'adressatType', valueIndex: [2] },
				attributtType: AttributtType.SelectAndEdit
			},
			{
				hovedKategori: Kategorier.KontaktInformasjonForDoedsbo,
				subKategori: SubKategorier.Adressat,
				id: 'foedselsdato',
				label: 'Fødselsdato',
				path: 'adressat.foedselsdato',
				subGruppe: 'Adressat',
				dataSource: DataSource.PDLF,
				validation: DateValidation(false),
				inputType: InputType.Date,
				onlyShowAfterSelectedValue: { attributtId: 'adressatType', valueIndex: [3] },
				attributtType: AttributtType.SelectAndEdit
			},
			{
				hovedKategori: Kategorier.KontaktInformasjonForDoedsbo,
				subKategori: SubKategorier.Adressat,
				id: 'fornavn',
				label: 'Fornavn',
				path: 'adressat.fornavn',
				subGruppe: 'Adressat',
				dataSource: DataSource.PDLF,
				inputType: InputType.Text,
				validation: yup.string().required('Vennligst fyll inn navn'),
				onlyShowAfterSelectedValue: { attributtId: 'adressatType', valueIndex: [0, 1, 3] },
				attributtType: AttributtType.SelectAndEdit
			},
			{
				hovedKategori: Kategorier.KontaktInformasjonForDoedsbo,
				subKategori: SubKategorier.Adressat,
				id: 'mellomnavn',
				label: 'mellomnavn',
				path: 'adressat.mellomnavn',
				subGruppe: 'Adressat',
				dataSource: DataSource.PDLF,
				inputType: InputType.Text,
				onlyShowAfterSelectedValue: { attributtId: 'adressatType', valueIndex: [0, 1, 3] },
				attributtType: AttributtType.SelectAndEdit
			},
			{
				hovedKategori: Kategorier.KontaktInformasjonForDoedsbo,
				subKategori: SubKategorier.Adressat,
				id: 'etternavn',
				label: 'Etternavn',
				path: 'adressat.etternavn',
				subGruppe: 'Adressat',
				dataSource: DataSource.PDLF,
				inputType: InputType.Text,
				onlyShowAfterSelectedValue: { attributtId: 'adressatType', valueIndex: [0, 1, 3] },
				validation: yup.string().required('Vennligst fyll inn navn'),
				attributtType: AttributtType.SelectAndEdit
			},
			{
				hovedKategori: Kategorier.KontaktInformasjonForDoedsbo,
				subKategori: SubKategorier.Adressat,
				id: 'org_orgnavn',
				label: 'Organisasjonsnavn',
				path: 'adressat.org_orgnavn',
				subGruppe: 'Adressat',
				dataSource: DataSource.PDLF,
				onlyShowAfterSelectedValue: { attributtId: 'adressatType', valueIndex: [1] },
				inputType: InputType.Text,
				attributtType: AttributtType.SelectAndEdit,
				validation: yup.string().required('Vennligst fyll inn organisasjonsnavn')
			},
			{
				hovedKategori: Kategorier.KontaktInformasjonForDoedsbo,
				subKategori: SubKategorier.Adressat,
				id: 'advokat_orgnavn',
				label: 'Organisasjonsnavn',
				path: 'adressat.advokat_orgnavn',
				subGruppe: 'Adressat',
				dataSource: DataSource.PDLF,
				onlyShowAfterSelectedValue: { attributtId: 'adressatType', valueIndex: [0] },
				inputType: InputType.Text,
				attributtType: AttributtType.SelectAndEdit
			},
			{
				hovedKategori: Kategorier.KontaktInformasjonForDoedsbo,
				subKategori: SubKategorier.Adressat,
				id: 'org_orgnr',
				label: 'Organisasjonsnummer',
				path: 'adressat.org_orgnr',
				subGruppe: 'Adressat',
				dataSource: DataSource.PDLF,
				inputType: InputType.Text,
				onlyShowAfterSelectedValue: { attributtId: 'adressatType', valueIndex: [1] },
				// Egen validation pga yup tror stor streng ikke er integer
				validation: yup.string().matches(/^[0-9]{9}$/, {
					message: 'Orgnummer må være et tall med 9 sifre',
					excludeEmptyString: true
				}),
				attributtType: AttributtType.SelectAndRead
			},
			{
				hovedKategori: Kategorier.KontaktInformasjonForDoedsbo,
				subKategori: SubKategorier.Adressat,
				id: 'advokat_orgnr',
				label: 'Organisasjonsnummer',
				path: 'adressat.advokat_orgnr',
				subGruppe: 'Adressat',
				dataSource: DataSource.PDLF,
				inputType: InputType.Text,
				onlyShowAfterSelectedValue: { attributtId: 'adressatType', valueIndex: [0] },
				// Egen validation pga yup tror stor streng ikke er integer
				validation: yup.string().matches(/^[0-9]{9}$/, {
					message: 'Orgnummer må være et tall med 9 sifre',
					excludeEmptyString: true
				}),
				attributtType: AttributtType.SelectAndRead
			},
			{
				hovedKategori: Kategorier.KontaktInformasjonForDoedsbo,
				subKategori: SubKategorier.DoedsboAdresse,
				id: 'landkode',
				parent: 'adresse',
				label: 'Land',
				subGruppe: 'Adresse',
				dataSource: DataSource.PDLF,
				inputType: InputType.Select,
				apiKodeverkId: 'Landkoder',
				validation: yup.string().required('Vennligst fyll inn land'),
				defaultValue: 'NOR',
				attributtType: AttributtType.SelectAndRead
			},
			{
				hovedKategori: Kategorier.KontaktInformasjonForDoedsbo,
				subKategori: SubKategorier.DoedsboAdresse,
				id: 'adresselinje1',
				path: 'adresse.adresselinje1',
				parent: 'adresse',
				label: 'Adresselinje 1',
				subGruppe: 'Adresse',
				dataSource: DataSource.PDLF,
				inputType: InputType.Text,
				validation: yup.string().required('Vennligst fyll ut'),
				attributtType: AttributtType.SelectAndRead
			},
			{
				hovedKategori: Kategorier.KontaktInformasjonForDoedsbo,
				subKategori: SubKategorier.DoedsboAdresse,
				id: 'adresselinje2',
				path: 'adresse.adresselinje2',
				parent: 'adresse',
				label: 'Adresselinje 2',
				subGruppe: 'Adresse',
				dataSource: DataSource.PDLF,
				inputType: InputType.Text,
				attributtType: AttributtType.SelectAndRead
			},
			{
				hovedKategori: Kategorier.KontaktInformasjonForDoedsbo,
				subKategori: SubKategorier.DoedsboAdresse,
				id: 'postnummer',
				path: 'adresse.postnummer',
				parent: 'adresse',
				label: 'Postnummer og -sted',
				subGruppe: 'Adresse',
				dataSource: DataSource.PDLF, //endres
				apiKodeverkShowValueInLabel: true,
				inputType: InputType.Select,
				apiKodeverkId: 'Postnummer',
				validation: yup.string().required('Vennligst fyll inn postnummer/-sted'),
				onlyShowDependentOnOtherValue: { attributtId: 'landkode', value: ['NOR', ''] },
				attributtType: AttributtType.SelectAndEdit
			},
			{
				hovedKategori: Kategorier.KontaktInformasjonForDoedsbo,
				subKategori: SubKategorier.DoedsboAdresse,
				id: 'utenlandsk_postnummer',
				path: 'adresse.utenlandsk_postnummer',
				parent: 'adresse',
				label: 'Postnummer',
				subGruppe: 'Adresse',
				dataSource: DataSource.PDLF, //endres
				inputType: InputType.Text,
				onlyShowDependentOnOtherValue: { attributtId: 'landkode', exceptValue: ['NOR', ''] },
				validation: yup.string().required('Vennligst fyll inn postnummer'),
				attributtType: AttributtType.SelectAndEdit
			},
			{
				hovedKategori: Kategorier.KontaktInformasjonForDoedsbo,
				subKategori: SubKategorier.DoedsboAdresse,
				id: 'utenlandsk_poststedsnavn',
				path: 'adresse.utenlandsk_poststed',
				parent: 'adresse',
				label: 'Poststed',
				subGruppe: 'Adresse',
				dataSource: DataSource.PDLF,
				validation: yup.string().required('Vennligst fyll inn poststed'),
				onlyShowDependentOnOtherValue: { attributtId: 'landkode', exceptValue: ['NOR', ''] },
				inputType: InputType.Text,
				attributtType: AttributtType.SelectAndRead
			},
			{
				hovedKategori: Kategorier.KontaktInformasjonForDoedsbo,
				subKategori: SubKategorier.Diverse,
				id: 'skifteform',
				label: 'Skifteform',
				subGruppe: 'Diverse',
				dataSource: DataSource.PDLF,
				inputType: InputType.Select,
				validation: yup.string().required('Vennligst velg'),
				options: SelectOptionsManager('skifteform'),
				attributtType: AttributtType.SelectAndEdit
			},
			{
				hovedKategori: Kategorier.KontaktInformasjonForDoedsbo,
				subKategori: SubKategorier.Diverse,
				id: 'utstedtDato',
				label: 'Skifteform utstedt',
				subGruppe: 'Diverse',
				dataSource: DataSource.PDLF,
				inputType: InputType.Date,
				validation: DateValidation(),
				attributtType: AttributtType.SelectAndEdit,
				defaultValue: new Date()
			},
			{
				hovedKategori: Kategorier.KontaktInformasjonForDoedsbo,
				subKategori: SubKategorier.Diverse,
				id: 'gyldigFom',
				label: 'Gyldig fra',
				subGruppe: 'Diverse',
				dataSource: DataSource.PDLF,
				validation: DateValidation(),
				inputType: InputType.Date,
				attributtType: AttributtType.SelectAndEdit,
				defaultValue: new Date()
			},
			{
				hovedKategori: Kategorier.KontaktInformasjonForDoedsbo,
				subKategori: SubKategorier.Diverse,
				id: 'gyldigTom',
				label: 'Gyldig til',
				subGruppe: 'Diverse',
				dataSource: DataSource.PDLF,
				validation: DateValidation(false),
				inputType: InputType.Date,
				attributtType: AttributtType.SelectAndEdit
			}
		]
	}
]

export default AttributtListe
