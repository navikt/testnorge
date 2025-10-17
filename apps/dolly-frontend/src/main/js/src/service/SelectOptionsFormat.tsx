import { Option } from '@/service/SelectOptionsOppslag'
import * as _ from 'lodash-es'
import { toTitleCase } from '@/utils/DataFormatter'

type Data = {
	label: string
	id: string
	navn: string
	value: {
		data: any
	}
}

type PersonListe = {
	value: string
	label: string
	alder: number
	sivilstand: string
	vergemaal: boolean
	doedsfall: boolean
	foreldre: Array<string>
	foreldreansvar: Array<string>
}
export const SelectOptionsFormat = {
	formatOptions: (type: string, data: any, loading?: boolean) => {
		if (loading || data?.loading) {
			return []
		}
		if (!data?.value && _.isEmpty(data)) {
			return []
		}
		const kodeverk = data?.value?.data || data
		if (type === 'personnavn') {
			const persondata: any[] = kodeverk || []
			const options: Option[] = []
			persondata?.length > 0 &&
				persondata.forEach((personInfo) => {
					if (!_.isNil(personInfo.fornavn)) {
						const mellomnavn = !_.isNil(personInfo.mellomnavn) ? ' ' + personInfo.mellomnavn : ''
						const navn = personInfo.fornavn + mellomnavn + ' ' + personInfo.etternavn
						options.push({ value: personInfo.fornavn.toUpperCase(), label: navn.toUpperCase() })
					}
				})
			return options
		} else if (type === 'fornavn' || type === 'mellomnavn' || type === 'etternavn') {
			const navnData = kodeverk || []
			const options: { value: string; label: string }[] = []
			navnData?.length > 0 &&
				navnData.forEach((navn: { [x: string]: any }) => {
					options.push({ value: navn[type], label: navn[type] })
				})
			return options
		} else if (type === 'navnOgFnr') {
			const persondata = kodeverk.liste || kodeverk || []
			const options: Option[] = []
			persondata?.length > 0 &&
				persondata.forEach(
					(personInfo: { fornavn: string; mellomnavn: string; etternavn: string; fnr: string }) => {
						if (!_.isNil(personInfo.fornavn)) {
							const mellomnavn = !_.isNil(personInfo.mellomnavn) ? ' ' + personInfo.mellomnavn : ''
							const navnOgFnr =
								(personInfo.fornavn + mellomnavn + ' ' + personInfo.etternavn).toUpperCase() +
								': ' +
								personInfo.fnr
							options.push({ value: personInfo.fnr, label: navnOgFnr })
						}
					},
				)
			return options
		} else if (type === 'arbeidsforholdstyper') {
			const options = kodeverk?.koder || kodeverk || []
			options?.length > 0 &&
				options.forEach((option: Option) => {
					if (option.value === 'frilanserOppdragstakerHonorarPersonerMm') {
						option.label = 'Frilansere/oppdragstakere, honorar, m.m.'
					}
					if (option.value === 'pensjonOgAndreTyperYtelserUtenAnsettelsesforhold') {
						option.label = 'Pensjoner og andre typer ytelser uten ansettelsesforhold'
					}
				})
			return options
		} else if (type === 'understatuser') {
			const statuser = kodeverk ? Object.entries(kodeverk) : []
			const options: Option[] = []
			statuser.forEach((status) => {
				options.push({ value: parseInt(status[0]), label: `${status[0]}: ${status[1]}` })
			})
			return options
		} else if (type === 'roller') {
			const roller = kodeverk ? Object.entries(kodeverk) : []
			const options: Option[] = []
			roller.forEach((rolle: [string, string]) => {
				options.push({ value: rolle[0], label: rolle[1] })
			})
			return options
		} else if (type === 'telefonLandkoder') {
			const landkoder =
				kodeverk?.sort?.((land1, land2) => {
					if (land1.label > land2.label) return 1
					else if (land1.label < land2.label) return -1
				}) || []
			const options: Option[] = []
			landkoder?.forEach((landData: any) => {
				const telefonLandkode = landData?.countryCallingCodes
				if (!telefonLandkode) return
				options.push({
					landkode: telefonLandkode.replaceAll(' ', ''),
					value: landData.value,
					label: `${landData.emoji} ${toTitleCase(landData.label)} (${telefonLandkode})`,
				})
			})
			return options
		} else if (type === 'sdpLeverandoer') {
			const leverandoerer = kodeverk ? Object.entries(kodeverk) : []
			const options: Option[] = []
			leverandoerer.forEach((leverandoer: [string, any]) => {
				data = leverandoer[1]
				options.push({ value: parseInt(data.id), label: data.navn })
			})
			return options
		} else if (type === 'tags') {
			const tags = kodeverk ? Object.entries(kodeverk) : []
			const options: Option[] = []
			tags.forEach((leverandoer) => {
				data = leverandoer[1]
				options.push({ value: data.tag, label: data.beskrivelse })
			})
			return options
		} else if (type === 'fullmaktOmraader') {
			const omraader = kodeverk ? Object.entries(kodeverk?.koder) : []
			const ugyldigeKoder = ['BII', 'KLA', 'KNA', 'KOM', 'LGA', 'MOT', 'OVR']
			const options: Option[] = []
			options.push({ value: '*', label: '* (Alle)' })
			omraader
				.filter((omr: [string, Option]) => {
					return !ugyldigeKoder.includes(omr[1].value)
				})
				.forEach((omraade: [string, Data]) => {
					data = omraade[1]
					options.push({ value: data.value, label: data.label })
				})
			return options
		} else {
			return kodeverk
		}
	},
}
