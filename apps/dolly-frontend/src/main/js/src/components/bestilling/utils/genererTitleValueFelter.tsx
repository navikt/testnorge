import { TitleValue } from '@/components/ui/titleValue/TitleValue'
import { kodeverkKeyToLabel } from '@/components/fagsystem/sigrunstubPensjonsgivende/utils'
import { formatDate } from '@/utils/DataFormatter'
import React from 'react'
import texts from '@/components/inntektStub/texts'

export default function genererTitleValueFelter(data: any) {
	// const titleValueFelter = []
	// for (const [key, value] of Object.entries(data)) {
	//   if (Array.isArray(value)) {
	//     value.forEach((element, index) => {
	//       titleValueFelter.push({ title: `${key} ${index + 1}`, value: element })
	//     })
	//   } else {
	//     titleValueFelter.push({ title: key, value })
	//   }
	// }
	// return titleValueFelter
	return Object.entries(data)?.map(([key, value]) => {
		const erDato = !isNaN(Date.parse(value))
		console.log('texts(key): ', texts(key)) //TODO - SLETT MEG
		console.log('texts(value): ', texts(value)) //TODO - SLETT MEG
		if (
			erDato &&
			(key.includes('Dato') ||
				key.includes('dato') ||
				key.includes('Periode') ||
				key.includes('periode'))
		) {
			return <TitleValue title={texts(key)} value={formatDate(value)} />
		}
		return <TitleValue title={texts(key)} value={texts(value)} />
	})
}

//TODO: Tilleggsinformasjon er et objekt med et tomt objekt som da blir value. F.eks. BilOgBaat. Feiler i visning.
