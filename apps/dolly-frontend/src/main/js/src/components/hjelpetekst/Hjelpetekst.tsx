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
	style,
	...props
}: HjelpetekstProps) => {
	return (
		<HelpText
			style={style}
			placement={placement}
			onClick={(event) => {
				event.stopPropagation()
			}}
			{...props}
		>
			{children}
			{requestFeedback && (
				<ThumbsRating ratingFor={`Hjelpetekst`} label="Svarte teksten på spørsmålet ditt?" />
			)}
		</HelpText>
	)
}
