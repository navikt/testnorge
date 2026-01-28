import React from 'react'
import { TitleValue } from '@/components/ui/titleValue/TitleValue'
import { PostadresseIFrittFormatData } from '@/pages/gruppe/PersonVisning/PersonMiljoeinfo/PdlDataTyper'
import { isEmpty } from '@/components/fagsystem/pdlf/form/partials/utils'
import { arrayToString } from '@/utils/DataFormatter'

type PostadresseTypes = {
	postadresseIFrittFormat?: PostadresseIFrittFormatData
}

export const PostadresseIFrittFormat = ({ postadresseIFrittFormat }: PostadresseTypes) => {
	if (!postadresseIFrittFormat) {
		return null
	}

	if (isEmpty(postadresseIFrittFormat)) {
		return <TitleValue title="Postadresse i fritt format" value="Ingen verdier satt" />
	}

	return (
		<>
			<TitleValue
				title="Adresselinjer"
				value={arrayToString(postadresseIFrittFormat.adresselinjer, ', ')}
			/>

			<TitleValue title="Postnummer" value={postadresseIFrittFormat.postnummer} />
		</>
	)
}
