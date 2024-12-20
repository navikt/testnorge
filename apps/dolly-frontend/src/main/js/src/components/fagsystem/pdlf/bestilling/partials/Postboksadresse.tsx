import React from 'react'
import { TitleValue } from '@/components/ui/titleValue/TitleValue'
import { PostboksadresseData } from '@/pages/gruppe/PersonVisning/PersonMiljoeinfo/PdlDataTyper'

type PostboksadresseTypes = {
	postboksadresse?: PostboksadresseData
}

export const Postboksadresse = ({ postboksadresse }: PostboksadresseTypes) => {
	if (!postboksadresse) {
		return null
	}

	return (
		<>
			<TitleValue title="Postbokseier" value={postboksadresse.postbokseier} />
			<TitleValue title="Postboks" value={postboksadresse.postboks} />
			<TitleValue title="Postnummer" value={postboksadresse.postnummer} />
		</>
	)
}
