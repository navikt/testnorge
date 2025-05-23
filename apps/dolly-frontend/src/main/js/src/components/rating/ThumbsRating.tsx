import * as React from 'react'
import { BaseSyntheticEvent, useState } from 'react'
// @ts-ignore
import Icon from '@/components/ui/icon/Icon'
import NavButton from '../ui/button/NavButton/NavButton'
import styled from 'styled-components'
import './ThumbsRating.less'
import { Logger } from '@/logger/Logger'
import { useBrukerProfil } from '@/utils/hooks/useBruker'

enum Rating {
	Positive = 'POSITIVE',
	Negative = 'NEGATIVE',
}

interface ThumbsRatingProps {
	label: string
	ratingFor: string
	infoText?: string
	uuid?: string
	onClick?: (rating: Rating) => void
	children?: React.ReactNode
	etterBestilling?: boolean
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
	<Icon className={className} kind="thumbs-up" title="tommel opp" fontSize={'1.6rem'} />
)
const ThumpsDown = ({ className }: IconButton) => (
	// @ts-ignore
	<Icon className={className} kind="thumbs-down" title="tommel ned" fontSize={'1.6rem'} />
)

export const ThumbsRating = ({
	label,
	ratingFor,
	infoText,
	onClick,
	uuid,
	children,
	etterBestilling,
}: ThumbsRatingProps) => {
	const { brukerProfil } = useBrukerProfil()
	const [rated, setRated] = useState(false)

	const _onClick = (rating: Rating) => {
		if (onClick) {
			onClick(rating)
		}
		if (!etterBestilling) {
			Logger.log({
				event: `Vurdering av: ${ratingFor}`,
				rating: rating,
				message: `${infoText ? `Infotekst som fikk vurderingen: ${infoText}` : `Vurdering av ${ratingFor}`}`,
				uuid: uuid,
				brukerType: brukerProfil?.type,
				brukernavn: brukerProfil?.visningsNavn,
				tilknyttetOrganisasjon: brukerProfil?.organisasjon,
			})
		}
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
