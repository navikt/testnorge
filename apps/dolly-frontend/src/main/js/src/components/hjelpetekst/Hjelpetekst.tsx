import * as React from 'react'
// @ts-ignore
import { v4 as uuid } from 'uuid'
import { ThumbsRating } from '../rating'
import { HelpText, HelpTextProps } from '@navikt/ds-react'
import { bottom } from '@popperjs/core'

export const Hjelpetekst = ({ children, placement = bottom }: HelpTextProps) => {
	return (
		<div>
			<HelpText placement={placement}>
				{children}
				<ThumbsRating ratingFor={`Hjelpetekst`} label="Svarte teksten på spørsmålet ditt?" />
			</HelpText>
		</div>
	)
}
