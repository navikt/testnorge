import { Foedsel } from '@/components/fagsystem/pdlf/bestilling/partials/Foedsel'
import styled from 'styled-components'
import React from 'react'
import { Persondetaljer } from '@/components/fagsystem/pdlf/bestilling/partials/Persondetaljer'

export const BestillingTitle = styled.h4`
	margin: 10px 0;
`

const StyledText = styled.p`
	margin: 5px 0;
`

export const EmptyObject = () => <StyledText>Ingen verdier satt</StyledText>

export const Bestillingsdata = ({ bestilling }: any) => {
	console.log('bestilling: ', bestilling) //TODO - SLETT MEG
	return (
		<>
			<Persondetaljer opprettNyPerson={bestilling.pdldata?.opprettNyPerson} />
			<Foedsel foedselListe={bestilling.pdldata?.person?.foedsel} />
		</>
	)
}
