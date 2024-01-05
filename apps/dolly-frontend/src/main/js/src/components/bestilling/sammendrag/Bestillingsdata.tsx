import { Foedsel } from '@/components/fagsystem/pdlf/bestilling/partials/Foedsel'
import styled from 'styled-components'
import React from 'react'
import { Alder } from '@/components/fagsystem/pdlf/bestilling/partials/Alder'
import { Doedsfall } from '@/components/fagsystem/pdlf/bestilling/partials/Doedsfall'
import { Statsborgerskap } from '@/components/fagsystem/pdlf/bestilling/partials/Statsborgerskap'
import { Innvandring } from '@/components/fagsystem/pdlf/bestilling/partials/Innvandring'
import { Utvandring } from '@/components/fagsystem/pdlf/bestilling/partials/Utvandring'
import { Kjoenn } from '@/components/fagsystem/pdlf/bestilling/partials/Kjoenn'
import { Navn } from '@/components/fagsystem/pdlf/bestilling/partials/Navn'

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
			<Alder opprettNyPerson={bestilling.pdldata?.opprettNyPerson} />
			<Foedsel foedselListe={bestilling.pdldata?.person?.foedsel} />
			<Doedsfall doedsfallListe={bestilling.pdldata?.person?.doedsfall} />
			<Statsborgerskap statsborgerskapListe={bestilling.pdldata?.person?.statsborgerskap} />
			<Innvandring innvandringListe={bestilling.pdldata?.person?.innflytting} />
			<Utvandring utvandringListe={bestilling.pdldata?.person?.utflytting} />
			<Kjoenn kjoennListe={bestilling.pdldata?.person?.kjoenn} />
			<Navn navnListe={bestilling.pdldata?.person?.navn} />
		</>
	)
}
