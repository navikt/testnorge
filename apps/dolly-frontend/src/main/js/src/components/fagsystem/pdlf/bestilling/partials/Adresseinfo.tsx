import React from 'react'
import { TitleValue } from '@/components/ui/titleValue/TitleValue'
import { formatDate } from '@/utils/DataFormatter'

type AdresseinfoTypes = {
	adresseinfo: {
		angittFlyttedato?: Date
		gyldigFraOgMed?: Date
		gyldigTilOgMed?: Date
		opprettCoAdresseNavn?: {
			fornavn?: string
			mellomnavn?: string
			etternavn?: string
		}
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
			<TitleValue title="C/O adressenavn" value={navn} />
		</>
	)
}
