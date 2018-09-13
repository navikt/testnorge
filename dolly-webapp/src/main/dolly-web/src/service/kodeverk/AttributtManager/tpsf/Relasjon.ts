import { Kategorier, SubKategorier } from '../Categories'
import { Attributt, InputType, DataSource } from '../Types'

import * as yup from 'yup'

const AttributtListe: Attributt[] = [
	{
		hovedKategori: Kategorier.FamilieRelasjoner,
		subKategori: SubKategorier.Partner,
		id: 'relasjoner.partner',
		label: 'Har partner',
		dataSource: DataSource.TPSF,
		inputType: InputType.Multifield,
		items: [
			{
				hovedKategori: Kategorier.PersInfo,
				subKategori: null,
				id: 'relasjoner.partner.identtype',
				label: 'Type',
				dataSource: DataSource.TPSF,
				inputType: InputType.Select,
				options: [{ value: 'FNR', label: 'FNR' }, { value: 'DNR', label: 'DNR' }],
				validation: yup.string().required('Velg identtype')
			},
			{
				hovedKategori: Kategorier.PersInfo,
				subKategori: SubKategorier.Diverse,
				id: 'relasjoner.partner.kjonn',
				label: 'Kjønn',
				dataSource: DataSource.TPSF,
				inputType: InputType.Select,
				options: [{ value: 'K', label: 'Kvinne' }, { value: 'M', label: 'Mann' }],
				validation: yup.string().required('Velg kjønn')
			},
			{
				hovedKategori: Kategorier.PersInfo,
				subKategori: SubKategorier.Alder,
				id: 'relasjoner.partner.foedtEtter',
				label: 'Født etter',
				dataSource: DataSource.TPSF,
				inputType: InputType.Date,
				validation: yup.date()
			},
			{
				hovedKategori: Kategorier.PersInfo,
				subKategori: SubKategorier.Alder,
				id: 'relasjoner.partner.foedtFoer',
				label: 'Født før',
				dataSource: DataSource.TPSF,
				inputType: InputType.Date,
				validation: yup.date()
			}
		],
		validation: yup.string()
	},
	{
		hovedKategori: Kategorier.FamilieRelasjoner,
		subKategori: SubKategorier.Barn,
		id: 'relasjoner.barn',
		label: 'Har barn',
		dataSource: DataSource.TPSF,
		inputType: InputType.Multifield,
		items: [
			{
				hovedKategori: Kategorier.PersInfo,
				subKategori: null,
				id: 'relasjoner.barn.identtype',
				label: 'Type',
				dataSource: DataSource.TPSF,
				inputType: InputType.Select,
				options: [{ value: 'FNR', label: 'FNR' }, { value: 'DNR', label: 'DNR' }],
				validation: yup.string().required('Velg identtype')
			},
			{
				hovedKategori: Kategorier.PersInfo,
				subKategori: SubKategorier.Diverse,
				id: 'relasjoner.barn.kjonn',
				label: 'Kjønn',
				dataSource: DataSource.TPSF,
				inputType: InputType.Select,
				options: [{ value: 'K', label: 'Kvinne' }, { value: 'M', label: 'Mann' }],
				validation: yup.string().required('Velg kjønn')
			},
			{
				hovedKategori: Kategorier.PersInfo,
				subKategori: SubKategorier.Alder,
				id: 'relasjoner.barn.foedtEtter',
				label: 'Født etter',
				dataSource: DataSource.TPSF,
				inputType: InputType.Date,
				validation: yup.date()
			},
			{
				hovedKategori: Kategorier.PersInfo,
				subKategori: SubKategorier.Alder,
				id: 'relasjoner.barn.foedtFoer',
				label: 'Født før',
				dataSource: DataSource.TPSF,
				inputType: InputType.Date,
				validation: yup.date()
			}
		],
		validation: yup.string()
	}
]

export default AttributtListe
