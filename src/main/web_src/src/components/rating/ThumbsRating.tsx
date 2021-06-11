import * as React from 'react'
import { useState } from 'react'
import Logger from '../../logger'
import { Rating } from '../../logger/types'
// @ts-ignore
import Icon from '~/components/ui/icon/Icon'
import NavButton from '../ui/button/NavButton/NavButton'
import './ThumbsRating.less'
import styled from 'styled-components'

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
		padding: 0 8px;
		border-radius: 50%;
		margin-left: 7px;
	}
	&& :focus {
		outline: none;
		box-shadow: 0 0 2px @color-nav-black;
	}
`

interface IconButton {
	className?: string
}

const ThumpsUp = ({ className }: IconButton) => (
	// @ts-ignore
	<Icon className={className} kind="ThumbsUp" title="tommel opp" size={24} />
)
const ThumpsDown = ({ className }: IconButton) => (
	// @ts-ignore
	<Icon className={className} kind="ThumbsDown" title="tommel ned" size={24} />
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
			<ThumbsButton form="kompakt" onClick={() => _onClick(Rating.Positive)}>
				<ThumpsUp className="thumbs-rating__icon thumbs-rating__icon__left" />
			</ThumbsButton>
			<ThumbsButton form="kompakt" onClick={() => _onClick(Rating.Negative)}>
				<ThumpsDown className="thumbs-rating__icon thumbs-rating__icon__right" />
			</ThumbsButton>
		</div>
	)
}
