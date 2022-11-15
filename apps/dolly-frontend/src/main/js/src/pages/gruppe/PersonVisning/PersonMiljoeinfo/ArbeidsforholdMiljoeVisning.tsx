import React from 'react'
import Loading from '~/components/ui/loading/Loading'
import { AaregVisning } from '~/components/fagsystem'
import { useArbeidsforhold } from '~/utils/hooks/useOrganisasjoner'
import { Alert } from '@navikt/ds-react'

type PersonMiljoeinfoProps = {
	ident: string
	miljoe: string
}

export const ArbeidsforholdMiljoeVisning = ({ ident, miljoe }: PersonMiljoeinfoProps) => {
	const { loading, arbeidsforhold, error } = useArbeidsforhold(ident, true, miljoe)

	if (error) {
		return null
	}

	if (loading) {
		return <Loading label="Laster miljøer" />
	}

	return !arbeidsforhold || arbeidsforhold?.length < 1 ? (
		<Alert variant="info" size="small" inline>
			Fant ingen arbeidsforhold-data i dette miljøet
		</Alert>
	) : (
		<div className="boks">
			<AaregVisning liste={arbeidsforhold} />
		</div>
	)
}
