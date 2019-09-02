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
			{
				hovedKategori: Kategorier.Udi,
				subKategori: SubKategorier.Oppholdsstatus,
				id: 'typeOpphold',
				label: 'Type opphold',
				size: 'large',
				dataSource: DataSource.UDI,
				inputType: InputType.Select,
				onlyShowAfterSelectedValue: { attributtId: 'oppholdsstatus', valueIndex: [1] },
				options: SelectOptionsManager('typeOpphold'),
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
				onlyShowAfterSelectedValue: { attributtId: 'oppholdsstatus', valueIndex: [1, 2] },
				attributtType: AttributtType.SelectAndRead
			},
			{
				hovedKategori: Kategorier.Udi,
				subKategori: SubKategorier.Oppholdsstatus,
				id: 'oppholdTilDato',
				label: 'Oppholdstillatelse til dato',
				dataSource: DataSource.UDI,
				inputType: InputType.Date,
				onlyShowAfterSelectedValue: { attributtId: 'oppholdsstatus', valueIndex: [1, 2] },
				attributtType: AttributtType.SelectAndRead
			},
			{
				hovedKategori: Kategorier.Udi,
				subKategori: SubKategorier.Oppholdsstatus,
				id: 'ikkeOppholdGrunn',
				label: 'Grunn',
				size: 'medium',
				dataSource: DataSource.UDI,
				inputType: InputType.Select,
				onlyShowAfterSelectedValue: { attributtId: 'oppholdsstatus', valueIndex: [3] },
				options: SelectOptionsManager('ikkeOppholdGrunn'),
				validation: yup.string().required('Velg en grunn'),
				attributtType: AttributtType.SelectAndRead
			}
		]
	},
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
	}
]

export default AttributtListe
