import { Kategorier, SubKategorier } from '../Categories'
import { Attributt, InputType, DataSource, AttributtType } from '../Types'
import SelectOptionsManager from '~/service/kodeverk/SelectOptionsManager/SelectOptionsManager'
import DateValidation from '~/components/fields/Datepicker/DateValidation'
import * as yup from 'yup'

const AttributtListe: Attributt[] = [
	{
		hovedKategori: Kategorier.PersInfo,
		subKategori: SubKategorier.Alder,
		id: 'foedtEtter',
		label: 'Født etter',
		dataSource: DataSource.TPSF,
		inputType: InputType.Date,
		validation: DateValidation(false),
		attributtType: AttributtType.SelectOnly,
		sattForEksisterendeIdent: true,
		defaultValue: new Date().setFullYear(new Date().getFullYear() - 80)
	},
	{
		hovedKategori: Kategorier.PersInfo,
		subKategori: SubKategorier.Alder,
		id: 'foedtFoer',
		label: 'Født før',
		dataSource: DataSource.TPSF,
		inputType: InputType.Date,
		validation: DateValidation(false),
		attributtType: AttributtType.SelectOnly,
		sattForEksisterendeIdent: true,
		defaultValue: new Date()
	},
	{
		hovedKategori: Kategorier.PersInfo,
		subKategori: SubKategorier.Alder,
		id: 'doedsdato',
		label: 'Dødsdato',
		dataSource: DataSource.TPSF,
		inputType: InputType.Date,
		validation: DateValidation(false),
		attributtType: AttributtType.SelectOnly
		//defaultValue: new Date()
	},
	{
		hovedKategori: Kategorier.PersInfo,
		subKategori: SubKategorier.Nasjonalitet,
		id: 'statsborgerskap',
		label: 'Statsborgerskap',
		dataSource: DataSource.TPSF,
		inputType: InputType.Select,
		apiKodeverkId: 'Landkoder',
		attributtType: AttributtType.SelectAndEdit
	},
	{
		hovedKategori: Kategorier.PersInfo,
		subKategori: SubKategorier.Nasjonalitet,
		id: 'innvandret',
		label: 'Innvandret fra',
		dataSource: DataSource.TPSF,
		attributtType: AttributtType.SelectOnly,
		validation: yup.object(),
		items: [
			{
				hovedKategori: Kategorier.PersInfo,
				subKategori: SubKategorier.Nasjonalitet,
				id: 'innvandretFraLand',
				label: 'Innvandret fra land',
				dataSource: DataSource.TPSF,
				inputType: InputType.Select,
				editPath: 'innvandretFraLand',
				apiKodeverkId: 'Landkoder',
				attributtType: AttributtType.SelectAndEdit
			},
			{
				hovedKategori: Kategorier.PersInfo,
				subKategori: SubKategorier.Nasjonalitet,
				id: 'innvandretFraLandFlyttedato',
				label: 'Innvandret dato',
				dataSource: DataSource.TPSF,
				inputType: InputType.Date,
				editPath: 'innvandretTilLandFlyttedato',
				attributtType: AttributtType.SelectAndEdit
			}
		]
	},
	{
		hovedKategori: Kategorier.PersInfo,
		subKategori: SubKategorier.Nasjonalitet,
		id: 'utvandret',
		label: 'Utvandret til',
		dataSource: DataSource.TPSF,
		attributtType: AttributtType.SelectAndEdit,
		validation: yup.object(),
		items: [
			{
				hovedKategori: Kategorier.PersInfo,
				subKategori: SubKategorier.Nasjonalitet,
				id: 'utvandretTilLand',
				label: 'Utvandret til land',
				dataSource: DataSource.TPSF,
				inputType: InputType.Select,
				editPath: 'utvandretTilLand',
				apiKodeverkId: 'Landkoder',
				attributtType: AttributtType.SelectAndEdit
			},
			{
				hovedKategori: Kategorier.PersInfo,
				subKategori: SubKategorier.Nasjonalitet,
				id: 'utvandretTilLandFlyttedato',
				label: 'Utvandret dato',
				dataSource: DataSource.TPSF,
				inputType: InputType.Date,
				editPath: 'utvandretTilLandFlyttedato',
				attributtType: AttributtType.SelectAndEdit
			}
		]
	},
	{
		hovedKategori: Kategorier.PersInfo,
		subKategori: SubKategorier.Diverse,
		id: 'kjonn',
		label: 'Kjønn',
		dataSource: DataSource.TPSF,
		inputType: InputType.Select,
		apiKodeverkId: 'Kjønnstyper',
		attributtType: AttributtType.SelectOnly,
		sattForEksisterendeIdent: true
	},
	{
		hovedKategori: Kategorier.PersInfo,
		subKategori: SubKategorier.Diverse,
		id: 'harMellomnavn',
		label: 'Mellomnavn',
		dataSource: DataSource.TPSF,
		inputType: InputType.Select,
		options: SelectOptionsManager('boolean'),
		attributtType: AttributtType.SelectAndEdit
	},
	{
		hovedKategori: Kategorier.PersInfo,
		subKategori: SubKategorier.Diverse,
		id: 'sivilstand',
		label: 'Sivilstand',
		dataSource: DataSource.TPSF,
		inputType: InputType.Select,
		apiKodeverkId: 'Sivilstander',
		attributtType: AttributtType.SelectAndEdit
	},
	{
		hovedKategori: Kategorier.PersInfo,
		subKategori: SubKategorier.Diverse,
		id: 'sprakKode',
		label: 'Språk',
		dataSource: DataSource.TPSF,
		inputType: InputType.Select,
		apiKodeverkId: 'Språk',
		attributtType: AttributtType.SelectAndEdit
	},
	{
		hovedKategori: Kategorier.PersInfo,
		subKategori: SubKategorier.Diverse,
		id: 'egenAnsattDatoFom',
		label: 'Egenansatt',
		dataSource: DataSource.TPSF,
		inputType: InputType.Select,
		options: SelectOptionsManager('boolean'),
		attributtType: AttributtType.SelectAndEdit,
		transform: egenAnsatt => (egenAnsatt ? new Date() : null)
	},
	{
		hovedKategori: Kategorier.PersInfo,
		subKategori: SubKategorier.Diverse,
		id: 'utenFastBopel',
		label: 'Uten fast bopel',
		dataSource: DataSource.TPSF,
		inputType: InputType.Select,
		options: SelectOptionsManager('boolean'),
		attributtType: AttributtType.SelectOnly
	},
	{
		hovedKategori: Kategorier.PersInfo,
		subKategori: SubKategorier.Diverse,
		id: 'ufb_kommunenr',
		label: 'Kommunenummer',
		dataSource: DataSource.TPSF,
		inputType: InputType.Select,
		attributtType: AttributtType.EditOnly
	},
	{
		hovedKategori: Kategorier.PersInfo,
		subKategori: SubKategorier.Diverse,
		id: 'spesreg',
		label: 'Diskresjonskode',
		dataSource: DataSource.TPSF,
		inputType: InputType.Select,
		hoydeOptions: 'large',
		apiKodeverkId: 'Diskresjonskoder',
		attributtType: AttributtType.SelectAndEdit
	},
	{
		hovedKategori: Kategorier.PersInfo,
		subKategori: SubKategorier.Diverse,
		id: 'forsvunnet',
		label: 'Forsvunnet',
		dataSource: DataSource.TPSF,
		inputType: InputType.Select,
		attributtType: AttributtType.SelectOnly,
		items: [
			{
				hovedKategori: Kategorier.PersInfo,
				subKategori: SubKategorier.Diverse,
				id: 'erForsvunnet',
				label: 'Forsvunnet',
				dataSource: DataSource.TPSF,
				inputType: InputType.Select,
				options: SelectOptionsManager('stringBoolean'),
				attributtType: AttributtType.SelectOnly,
				editPath: 'erForsvunnet'
			},
			{
				hovedKategori: Kategorier.PersInfo,
				subKategori: SubKategorier.Diverse,
				id: 'forsvunnetDato',
				label: 'ForsvunnetDato',
				dataSource: DataSource.TPSF,
				inputType: InputType.Date,
				validation: DateValidation(),
				attributtType: AttributtType.SelectOnly,
				editPath: 'forsvunnetDato'
			}
		]
	}
]

export default AttributtListe
