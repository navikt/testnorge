import * as React from 'react'
// @ts-ignore
import { v4 as uuid } from 'uuid'
import { ThumbsRating } from '../rating'
import { HelpText, HelpTextProps } from '@navikt/ds-react'
import { bottom } from '@popperjs/core'

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
