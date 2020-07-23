import * as React from 'react'
// @ts-ignore
import { v4 as uuid } from 'uuid'
import NavHjelpeTekst from 'nav-frontend-hjelpetekst'
import Logger from '../../logger'
import { ThumbsRating } from '../rating'
import { PopoverOrientering } from 'nav-frontend-popover'

interface Props {
	hjelpetekstFor: string
	children: React.ReactNode
	type?: PopoverOrientering
}

export class Hjelpetekst extends React.Component<Props> {
	uuid: string

	constructor(props: Props) {
		super(props)
		this.uuid = uuid()
	}

	render() {
		const { children, hjelpetekstFor, type } = this.props
		const onClick = (e: React.MouseEvent<HTMLDivElement>): void => {
			e.stopPropagation()
			Logger.log({
				event: `Trykk på hjelpetekst for: ${hjelpetekstFor}`,
				uuid: this.uuid
			})
		}

		return (
			<div onClick={onClick}>
				<NavHjelpeTekst type={type}>
					{children}
					<ThumbsRating
						ratingFor={`Hjelpetekst for ${hjelpetekstFor}`}
						label="Svarte teksten på spørsmålet ditt?"
					/>
				</NavHjelpeTekst>
			</div>
		)
	}
}
