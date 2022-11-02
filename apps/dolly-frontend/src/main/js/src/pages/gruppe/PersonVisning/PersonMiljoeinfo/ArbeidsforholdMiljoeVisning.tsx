import React from 'react'
import Loading from '~/components/ui/loading/Loading'
import { AaregVisning } from '~/components/fagsystem'
import { useArbeidsforhold } from '~/utils/hooks/useOrganisasjoner'

type PersonMiljoeinfoProps = {
	ident: string
	miljoe: string
}

export const ArbeidsforholdMiljoeVisning = ({ ident, miljoe }: PersonMiljoeinfoProps) => {
	const { loading, arbeidsforhold, error } = useArbeidsforhold(ident, miljoe)

	if (error || !arbeidsforhold) {
		return null
	}

	if (loading) {
		return <Loading label="Laster miljøer" fullpage />
	}

	return <AaregVisning liste={arbeidsforhold} />
}
