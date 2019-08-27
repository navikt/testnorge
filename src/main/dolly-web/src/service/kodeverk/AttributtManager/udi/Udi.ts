import { Kategorier, SubKategorier } from '../Categories'
import { Attributt, InputType, DataSource, AttributtType } from '../Types'
import SelectOptionsManager from '~/service/kodeverk/SelectOptionsManager/SelectOptionsManager'
import DateValidation from '~/components/fields/Datepicker/DateValidation'

import * as yup from 'yup'

const AttributtListe: Attributt[] = [
	{
		hovedKategori: Kategorier.Udi,
		subKategori: SubKategorier.Oppholdsstatus,
		id: 'gjeldendeOppholdsstatus',
		label: 'Har oppholdsstatus',
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
				// editPath: 'oppholdsstatus',
				dataSource: DataSource.UDI,
				inputType: InputType.Select,
				options: SelectOptionsManager('opphold'),
				validation: yup.string().required('Velg en oppholdsstatus'),
				attributtType: AttributtType.SelectAndRead
			},
			{
				hovedKategori: Kategorier.Udi,
				subKategori: SubKategorier.Oppholdsstatus,
				id: 'typeOpphold',
				label: 'Type opphold',
				size: 'large',
				// editPath: 'typeOpphold',
				dataSource: DataSource.UDI,
				inputType: InputType.Select,
				onlyShowAfterSelectedValue: { attributtId: 'oppholdsstatus', valueIndex: [1] },
				options: SelectOptionsManager('oppholdstype'),
				// validation: yup.string().required('Velg en type opphold'),
				attributtType: AttributtType.SelectAndRead
			},
			{
				hovedKategori: Kategorier.Udi,
				subKategori: SubKategorier.Oppholdsstatus,
				id: 'oppholdFraDato',
				label: 'Oppholdstillatelse fra dato',
				// editPath: 'oppholdFraDato',
				dataSource: DataSource.UDI,
				inputType: InputType.Date,
				onlyShowAfterSelectedValue: { attributtId: 'oppholdsstatus', valueIndex: [1, 2] },
				// validation: yup.string().required('Velg en type opphold'),
				attributtType: AttributtType.SelectAndRead
			},
			{
				hovedKategori: Kategorier.Udi,
				subKategori: SubKategorier.Oppholdsstatus,
				id: 'oppholdTilDato',
				label: 'Oppholdstillatelse til dato',
				// editPath: 'oppholdTilDato',
				dataSource: DataSource.UDI,
				inputType: InputType.Date,
				onlyShowAfterSelectedValue: { attributtId: 'oppholdsstatus', valueIndex: [1, 2] },
				// validation: yup.string().required('Velg en type opphold'),
				attributtType: AttributtType.SelectAndRead
			},
			{
				hovedKategori: Kategorier.Udi,
				subKategori: SubKategorier.Oppholdsstatus,
				id: 'ikkeOppholdGrunn',
				label: 'Grunn',
				size: 'medium',
				// editPath: 'ikkeOppholdGrunn',
				dataSource: DataSource.UDI,
				inputType: InputType.Select,
				onlyShowAfterSelectedValue: { attributtId: 'oppholdsstatus', valueIndex: [3] },
				options: SelectOptionsManager('ikkeOppholdGrunn'),
				// validation: yup.string().required('Velg en type opphold'),
				attributtType: AttributtType.SelectAndRead
			}
		]
	},
	{
		hovedKategori: Kategorier.Udi,
		subKategori: SubKategorier.Arbeidsadgang,
		id: 'arbeidsadgang',
		label: 'Arbeidsadgang',
		dataSource: DataSource.UDI,
		validation: yup.object(),
		attributtType: AttributtType.SelectAndEdit,
		items: [
			{
				hovedKategori: Kategorier.Udi,
				subKategori: SubKategorier.Arbeidsadgang,
				id: 'harArbeidsAdgang',
				label: 'Har arbeidsadgang',
				// editPath: 'oppholdsstatus',
				dataSource: DataSource.UDI,
				inputType: InputType.Select,
				options: SelectOptionsManager('harArbeidsadgang'),
				validation: yup.string().required('Velg en verdi'),
				attributtType: AttributtType.SelectAndRead
			},
			{
				hovedKategori: Kategorier.Udi,
				subKategori: SubKategorier.Arbeidsadgang,
				id: 'typeArbeidsadgang',
				label: 'Type arbeidsadgang',
				size: 'medium',
				// editPath: 'oppholdsstatus',
				dataSource: DataSource.UDI,
				inputType: InputType.Select,
				onlyShowAfterSelectedValue: { attributtId: 'harArbeidsAdgang', valueIndex: [0] },
				options: SelectOptionsManager('typeArbeidsadgang'),
				// validation: yup.string().required('Velg en verdi'),
				attributtType: AttributtType.SelectAndRead
			},
			{
				hovedKategori: Kategorier.Udi,
				subKategori: SubKategorier.Arbeidsadgang,
				id: 'arbeidsOmfang',
				label: 'Arbeidsomfang',
				size: 'medium',
				// editPath: 'oppholdsstatus',
				dataSource: DataSource.UDI,
				inputType: InputType.Select,
				onlyShowAfterSelectedValue: { attributtId: 'harArbeidsAdgang', valueIndex: [0] },
				options: SelectOptionsManager('arbeidsOmfang'),
				// validation: yup.string().required('Velg en verdi'),
				attributtType: AttributtType.SelectAndRead
			},
			{
				hovedKategori: Kategorier.Udi,
				subKategori: SubKategorier.Arbeidsadgang,
				id: 'arbeidsadgangFraDato',
				label: 'Arbeidsadgang fra dato',
				// editPath: 'oppholdFraDato',
				dataSource: DataSource.UDI,
				inputType: InputType.Date,
				onlyShowAfterSelectedValue: { attributtId: 'harArbeidsAdgang', valueIndex: [0] },
				// validation: yup.string().required('Velg en type opphold'),
				attributtType: AttributtType.SelectAndRead
			},
			{
				hovedKategori: Kategorier.Udi,
				subKategori: SubKategorier.Arbeidsadgang,
				id: 'arbeidsadgangTilDato',
				label: 'Arbeidsadgang til dato',
				// editPath: 'oppholdTilDato',
				dataSource: DataSource.UDI,
				inputType: InputType.Date,
				onlyShowAfterSelectedValue: { attributtId: 'harArbeidsAdgang', valueIndex: [0] },
				// validation: yup.string().required('Velg en type opphold'),
				attributtType: AttributtType.SelectAndRead
			}
		]
	}
]

export default AttributtListe
