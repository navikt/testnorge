import * as React from 'react'
import { useState } from 'react'
import Logger from '../../logger'
import { Rating } from '../../logger/types'
// @ts-ignore
import Icon from '~/components/ui/icon/Icon'
import NavButton from '../../components/ui/button/NavButton/NavButton'
import './ThumbsRating.less'

interface ThumbsRatingProps {
	label: string
	ratingFor: string
	uuid?: string
	onClick?: (rating: Rating) => void
	children?: React.ReactNode
}

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
			<NavButton
				form="kompakt"
				className="thumbs-rating__button"
				onClick={() => _onClick(Rating.Positive)}
			>
				<ThumpsUp className="thumbs-rating__icon thumbs-rating__icon__left" />
			</NavButton>
			<NavButton
				form="kompakt"
				className="thumbs-rating__button"
				onClick={() => _onClick(Rating.Negative)}
			>
				<ThumpsDown className="thumbs-rating__icon thumbs-rating__icon__right" />
			</NavButton>
		</div>
	)
}
