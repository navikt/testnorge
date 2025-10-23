import React from 'react'
import { TitleValue } from '@/components/ui/titleValue/TitleValue'
import { formatDate, showLabel } from '@/utils/DataFormatter'

type AdresseinfoTypes = {
	adresseinfo: {
		angittFlyttedato?: Date
		gyldigFraOgMed?: Date
		gyldigTilOgMed?: Date
		oppholdAnnetSted?: string
		opprettCoAdresseNavn?: {
			fornavn?: string
			mellomnavn?: string
			etternavn?: string
		}
		master?: string
	}
}

export const Adresseinfo = ({ adresseinfo }: AdresseinfoTypes) => {
	if (!adresseinfo) {
		return null
	}

	const fornavn = adresseinfo.opprettCoAdresseNavn?.fornavn
	const mellomnavn = adresseinfo.opprettCoAdresseNavn?.mellomnavn
	const etternavn = adresseinfo.opprettCoAdresseNavn?.etternavn
	const navn = fornavn || mellomnavn || etternavn ? `${fornavn} ${mellomnavn} ${etternavn}` : null

	return (
		<>
			<TitleValue title="Flyttedato" value={formatDate(adresseinfo.angittFlyttedato)} />
			<TitleValue title="Gyldig f.o.m." value={formatDate(adresseinfo.gyldigFraOgMed)} />
			<TitleValue title="Gyldig t.o.m." value={formatDate(adresseinfo.gyldigTilOgMed)} />
			<TitleValue
				title="Opphold annet sted"
				value={showLabel('oppholdAnnetSted', adresseinfo.oppholdAnnetSted)}
			/>
			<TitleValue title="C/O adressenavn" value={navn} />
			<TitleValue title="Master" value={adresseinfo.master} />
		</>
	)
}
