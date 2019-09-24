import { Kategorier, SubKategorier } from '../Categories'
import { Attributt, InputType, DataSource, AttributtType } from '../Types'
import SelectOptionsManager from '~/service/kodeverk/SelectOptionsManager/SelectOptionsManager'
import DateValidation from '~/components/fields/Datepicker/DateValidation'

import * as yup from 'yup'

const AttributtListe: Attributt[] = [
	{
		hovedKategori: Kategorier.Udi,
		subKategori: SubKategorier.Oppholdsstatus,
		id: 'oppholdStatus',
		label: 'Har oppholdsstatus',
		path: 'oppholdStatus',
		dataSource: DataSource.UDI,
		validation: yup.object(),
		attributtType: AttributtType.SelectAndEdit,
		items: [
			{
				hovedKategori: Kategorier.Udi,
				subKategori: SubKategorier.Oppholdsstatus,
				id: 'oppholdsstatus',
				label: 'Oppholdsstatus',
				size: 'large',
				dataSource: DataSource.UDI,
				inputType: InputType.Select,
				options: SelectOptionsManager('oppholdsstatus'),
				validation: yup.string().required('Velg en oppholdsstatus'),
				attributtType: AttributtType.SelectAndRead
			},
			// EØS- eller EFTA-opphold:
			{
				hovedKategori: Kategorier.Udi,
				subKategori: SubKategorier.Oppholdsstatus,
				id: 'eosEllerEFTAtypeOpphold',
				label: 'Type opphold',
				size: 'large',
				dataSource: DataSource.UDI,
				inputType: InputType.Select,
				onlyShowAfterSelectedValue: { attributtId: 'oppholdsstatus', valueIndex: [0] },
				options: SelectOptionsManager('eosEllerEFTAtypeOpphold'),
				validation: yup.string().required('Velg en type opphold'),
				attributtType: AttributtType.SelectAndRead
			},
			{
				hovedKategori: Kategorier.Udi,
				subKategori: SubKategorier.Oppholdsstatus,
				id: 'oppholdFraDato',
				label: 'Oppholdstillatelse fra dato',
				dataSource: DataSource.UDI,
				inputType: InputType.Date,
				onlyShowAfterSelectedValue: { attributtId: 'oppholdsstatus', valueIndex: [0] },
				attributtType: AttributtType.SelectAndRead
			},
			{
				hovedKategori: Kategorier.Udi,
				subKategori: SubKategorier.Oppholdsstatus,
				id: 'oppholdTilDato',
				label: 'Oppholdstillatelse til dato',
				dataSource: DataSource.UDI,
				inputType: InputType.Date,
				onlyShowAfterSelectedValue: { attributtId: 'oppholdsstatus', valueIndex: [0] },
				attributtType: AttributtType.SelectAndRead
			},
			{
				hovedKategori: Kategorier.Udi,
				subKategori: SubKategorier.Oppholdsstatus,
				id: 'effektueringsDato',
				label: 'Effektueringsdato',
				dataSource: DataSource.UDI,
				inputType: InputType.Date,
				onlyShowAfterSelectedValue: { attributtId: 'oppholdsstatus', valueIndex: [0] },
				attributtType: AttributtType.SelectAndRead
			},
			{
				hovedKategori: Kategorier.Udi,
				subKategori: SubKategorier.Oppholdsstatus,
				id: 'eosEllerEFTABeslutningOmOppholdsrett',
				label: 'Grunnlag for opphold',
				size: 'medium',
				dataSource: DataSource.UDI,
				inputType: InputType.Select,
				onlyShowAfterSelectedValue: { attributtId: 'eosEllerEFTAtypeOpphold', valueIndex: [0] },
				options: SelectOptionsManager('eosEllerEFTABeslutningOmOppholdsrett'),
				validation: yup.string().required('Velg et grunnlag'),
				attributtType: AttributtType.SelectAndRead
			},
			{
				hovedKategori: Kategorier.Udi,
				subKategori: SubKategorier.Oppholdsstatus,
				id: 'eosEllerEFTAVedtakOmVarigOppholdsrett',
				label: 'Grunnlag for opphold',
				size: 'medium',
				dataSource: DataSource.UDI,
				inputType: InputType.Select,
				onlyShowAfterSelectedValue: { attributtId: 'eosEllerEFTAtypeOpphold', valueIndex: [1] },
				options: SelectOptionsManager('eosEllerEFTABeslutningOmOppholdsrett'),
				validation: yup.string().required('Velg et grunnlag'),
				attributtType: AttributtType.SelectAndRead
			},
			{
				hovedKategori: Kategorier.Udi,
				subKategori: SubKategorier.Oppholdsstatus,
				id: 'eosEllerEFTAOppholdstillatelse',
				label: 'Grunnlag for opphold',
				size: 'medium',
				dataSource: DataSource.UDI,
				inputType: InputType.Select,
				onlyShowAfterSelectedValue: { attributtId: 'eosEllerEFTAtypeOpphold', valueIndex: [2] },
				options: SelectOptionsManager('eosEllerEFTAOppholdstillatelse'),
				validation: yup.string().required('Velg et grunnlag'),
				attributtType: AttributtType.SelectAndRead
			},
			// Tredjelands borgere / Opphold samme vilkår:
			{
				hovedKategori: Kategorier.Udi,
				subKategori: SubKategorier.Oppholdsstatus,
				id: 'tredjelandsBorgereValg',
				label: 'Status',
				size: 'large',
				dataSource: DataSource.UDI,
				inputType: InputType.Select,
				onlyShowAfterSelectedValue: { attributtId: 'oppholdsstatus', valueIndex: [1] },
				options: SelectOptionsManager('tredjelandsBorgereValg'),
				validation: yup.string().required('Velg en status'),
				attributtType: AttributtType.SelectAndRead
			},
			{
				hovedKategori: Kategorier.Udi,
				subKategori: SubKategorier.Oppholdsstatus,
				id: 'oppholdSammeVilkaarFraDato',
				label: 'Oppholdstillatelse fra dato',
				dataSource: DataSource.UDI,
				inputType: InputType.Date,
				onlyShowAfterSelectedValue: { attributtId: 'tredjelandsBorgereValg', valueIndex: [0] },
				attributtType: AttributtType.SelectAndRead
			},
			{
				hovedKategori: Kategorier.Udi,
				subKategori: SubKategorier.Oppholdsstatus,
				id: 'oppholdSammeVilkaarTilDato',
				label: 'Oppholdstillatelse til dato',
				dataSource: DataSource.UDI,
				inputType: InputType.Date,
				onlyShowAfterSelectedValue: { attributtId: 'tredjelandsBorgereValg', valueIndex: [0] },
				attributtType: AttributtType.SelectAndRead
			},
			{
				hovedKategori: Kategorier.Udi,
				subKategori: SubKategorier.Oppholdsstatus,
				id: 'oppholdSammeVilkaarEffektuering',
				label: 'Effektueringsdato',
				dataSource: DataSource.UDI,
				inputType: InputType.Date,
				onlyShowAfterSelectedValue: { attributtId: 'tredjelandsBorgereValg', valueIndex: [0] },
				attributtType: AttributtType.SelectAndRead
			},
			{
				hovedKategori: Kategorier.Udi,
				subKategori: SubKategorier.Oppholdsstatus,
				id: 'oppholdstillatelseType',
				label: 'Type oppholdstillatelse',
				dataSource: DataSource.UDI,
				inputType: InputType.Select,
				onlyShowAfterSelectedValue: { attributtId: 'tredjelandsBorgereValg', valueIndex: [0] },
				options: SelectOptionsManager('oppholdstillatelseType'),
				validation: yup.string().required('Velg en type oppholdstillatelse'),
				attributtType: AttributtType.SelectAndRead
			},
			{
				hovedKategori: Kategorier.Udi,
				subKategori: SubKategorier.Oppholdsstatus,
				id: 'oppholdstillatelseVedtaksDato',
				label: 'Vedtaksdato',
				dataSource: DataSource.UDI,
				inputType: InputType.Date,
				onlyShowAfterSelectedValue: { attributtId: 'tredjelandsBorgereValg', valueIndex: [0] },
				attributtType: AttributtType.SelectAndRead
			}
		]
	},
	// ARBEIDSADGANG:
	{
		hovedKategori: Kategorier.Udi,
		subKategori: SubKategorier.Arbeidsadgang,
		id: 'arbeidsadgang',
		label: 'Arbeidsadgang',
		path: 'arbeidsadgang',
		dataSource: DataSource.UDI,
		validation: yup.object(),
		attributtType: AttributtType.SelectAndEdit,
		items: [
			{
				hovedKategori: Kategorier.Udi,
				subKategori: SubKategorier.Arbeidsadgang,
				id: 'harArbeidsAdgang',
				label: 'Har arbeidsadgang',
				dataSource: DataSource.UDI,
				inputType: InputType.Select,
				options: SelectOptionsManager('jaNeiUavklart'),
				validation: yup.string().required('Velg en verdi'),
				attributtType: AttributtType.SelectAndRead
			},
			{
				hovedKategori: Kategorier.Udi,
				subKategori: SubKategorier.Arbeidsadgang,
				id: 'typeArbeidsadgang',
				label: 'Type arbeidsadgang',
				size: 'medium',
				dataSource: DataSource.UDI,
				inputType: InputType.Select,
				onlyShowAfterSelectedValue: { attributtId: 'harArbeidsAdgang', valueIndex: [0] },
				options: SelectOptionsManager('typeArbeidsadgang'),
				attributtType: AttributtType.SelectAndRead
			},
			{
				hovedKategori: Kategorier.Udi,
				subKategori: SubKategorier.Arbeidsadgang,
				id: 'arbeidsOmfang',
				label: 'Arbeidsomfang',
				size: 'medium',
				dataSource: DataSource.UDI,
				inputType: InputType.Select,
				onlyShowAfterSelectedValue: { attributtId: 'harArbeidsAdgang', valueIndex: [0] },
				options: SelectOptionsManager('arbeidsOmfang'),
				attributtType: AttributtType.SelectAndRead
			},
			{
				hovedKategori: Kategorier.Udi,
				subKategori: SubKategorier.Arbeidsadgang,
				id: 'arbeidsadgangFraDato',
				label: 'Arbeidsadgang fra dato',
				dataSource: DataSource.UDI,
				inputType: InputType.Date,
				onlyShowAfterSelectedValue: { attributtId: 'harArbeidsAdgang', valueIndex: [0] },
				attributtType: AttributtType.SelectAndRead
			},
			{
				hovedKategori: Kategorier.Udi,
				subKategori: SubKategorier.Arbeidsadgang,
				id: 'arbeidsadgangTilDato',
				label: 'Arbeidsadgang til dato',
				dataSource: DataSource.UDI,
				inputType: InputType.Date,
				onlyShowAfterSelectedValue: { attributtId: 'harArbeidsAdgang', valueIndex: [0] },
				attributtType: AttributtType.SelectAndRead
			}
		]
	},
	// ALIASER:
	{
		hovedKategori: Kategorier.Udi,
		subKategori: SubKategorier.Alias,
		id: 'aliaser',
		label: 'Har aliaser',
		dataSource: DataSource.UDI,
		isMultiple: true,
		validation: yup.object(),
		attributtType: AttributtType.SelectAndEdit,
		items: [
			{
				hovedKategori: Kategorier.Udi,
				subKategori: SubKategorier.Alias,
				id: 'nyIdent',
				label: 'Type alias',
				path: 'nyIdent',
				dataSource: DataSource.UDI,
				inputType: InputType.Select,
				options: SelectOptionsManager('nyIdent'),
				validation: yup.string().required('Vennligst velg'),
				attributtType: AttributtType.SelectAndEdit
			},
			{
				hovedKategori: Kategorier.Udi,
				subKategori: SubKategorier.Alias,
				id: 'identtype',
				label: 'Identtype',
				path: 'identtype',
				dataSource: DataSource.UDI,
				inputType: InputType.Select,
				onlyShowAfterSelectedValue: { attributtId: 'nyIdent', valueIndex: [1] },
				options: SelectOptionsManager('identtype'),
				validation: yup.string().required('Vennligst velg'),
				attributtType: AttributtType.SelectAndEdit
			}
		]
	},
	// DIVERSE:
	{
		hovedKategori: Kategorier.Udi,
		subKategori: SubKategorier.Diverse,
		id: 'flyktning',
		label: 'Flyktningstatus',
		dataSource: DataSource.UDI,
		inputType: InputType.Select,
		options: SelectOptionsManager('boolean'),
		attributtType: AttributtType.SelectAndEdit
	},
	{
		hovedKategori: Kategorier.Udi,
		subKategori: SubKategorier.Diverse,
		id: 'soeknadOmBeskyttelseUnderBehandling',
		label: 'Asylsøker',
		dataSource: DataSource.UDI,
		inputType: InputType.Select,
		options: SelectOptionsManager('jaNeiUavklart'),
		attributtType: AttributtType.SelectAndEdit
	}
]

export default AttributtListe
