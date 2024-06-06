import * as React from 'react'
import { HelpText, HelpTextProps } from '@navikt/ds-react'
import { bottom } from '@popperjs/core'
import { ThumbsRating } from '@/components/rating/ThumbsRating'

interface HjelpetekstProps extends HelpTextProps {
	requestFeedback?: boolean
}

export const Hjelpetekst = ({
	children,
	placement = bottom,
	requestFeedback = true,
}: HjelpetekstProps) => {
	return (
		<HelpText
			placement={placement}
			onClick={(event) => {
				event.stopPropagation()
			}}
		>
			{children}
			{requestFeedback && (
				<ThumbsRating ratingFor={`Hjelpetekst`} label="Svarte teksten på spørsmålet ditt?" />
			)}
		</HelpText>
	)
}
