import * as React from 'react'
import { BaseSyntheticEvent, useState } from 'react'
// @ts-ignore
import Icon from '@/components/ui/icon/Icon'
import NavButton from '../ui/button/NavButton/NavButton'
import styled from 'styled-components'
import './ThumbsRating.less'
import { Logger } from '@/logger/Logger'

enum Rating {
	Positive = 'POSITIVE',
	Negative = 'NEGATIVE',
}

interface ThumbsRatingProps {
	label: string
	ratingFor: string
	uuid?: string
	onClick?: (rating: Rating) => void
	children?: React.ReactNode
}

const ThumbsButton = styled(NavButton)`
	&& {
		border: 0;
		padding: 7px 10px;
		border-radius: 50%;
		margin-left: 7px;
		box-shadow: inset 0 0 0 1.5px #0067c5;
	}

	&& :hover {
		outline: none;
		box-shadow: 0 0 2px @color-nav-black;
	}
`

interface IconButton {
	className?: string
}

const ThumpsUp = ({ className }: IconButton) => (
	// @ts-ignore
	<Icon
		className={className}
		kind="designsystem-thumbs-up"
		title="tommel opp"
		fontSize={'1.6rem'}
	/>
)
const ThumpsDown = ({ className }: IconButton) => (
	// @ts-ignore
	<Icon
		className={className}
		kind="designsystem-thumbs-down"
		title="tommel ned"
		fontSize={'1.6rem'}
	/>
)

export const ThumbsRating = ({ label, ratingFor, onClick, uuid, children }: ThumbsRatingProps) => {
	const [rated, setRated] = useState(false)

	const _onClick = (rating: Rating) => {
		if (onClick) {
			onClick(rating)
		}
		Logger.log({ event: `Vurdering av: ${ratingFor}`, rating: rating, uuid: uuid })
		setRated(true)
	}

	if (rated) {
		if (children) {
			return <>{children}</>
		}

		return (
			<div className="thumbs-rating flexbox--all-center">
				<p>Takk for din tilbakemelding.</p>
			</div>
		)
	}
	return (
		<div className="thumbs-rating flexbox--all-center">
			<p>{label}</p>
			<ThumbsButton
				variant={'secondary'}
				size={'small'}
				onClick={(event: BaseSyntheticEvent) => {
					event.stopPropagation()
					_onClick(Rating.Positive)
				}}
			>
				<ThumpsUp className="thumbs-rating__icon thumbs-rating__icon__left" />
			</ThumbsButton>
			<ThumbsButton
				variant={'secondary'}
				size={'small'}
				onClick={(event: BaseSyntheticEvent) => {
					event.stopPropagation()
					_onClick(Rating.Negative)
				}}
			>
				<ThumpsDown className="thumbs-rating__icon thumbs-rating__icon__right" />
			</ThumbsButton>
		</div>
	)
}
