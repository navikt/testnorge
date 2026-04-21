import * as React from 'react'
import { BaseSyntheticEvent, useState } from 'react'
// @ts-ignore
import './ThumbsRating.less'
import { Logger } from '@/logger/Logger'
import { useBrukerProfil } from '@/utils/hooks/useBruker'
import { ThumbDownFillIcon, ThumbUpFillIcon } from '@navikt/aksel-icons'
import { Button } from '@navikt/ds-react'

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
			<p>
				<b>Takk for din tilbakemelding.</b>
			</p>
		)
	}

	return (
		<>
			<p>
				<b>{label}</b>
			</p>
			<div className="thumbs-rating flexbox--align-start">
				<Button
					variant="secondary"
					size="medium"
					onClick={(event: BaseSyntheticEvent) => {
						event.stopPropagation()
						_onClick(Rating.Positive)
					}}
					icon={<ThumbUpFillIcon title="Ja" />}
				/>
				<Button
					variant="secondary"
					size="medium"
					onClick={(event: BaseSyntheticEvent) => {
						event.stopPropagation()
						_onClick(Rating.Negative)
					}}
					icon={<ThumbDownFillIcon title="Nei" />}
				/>
			</div>
		</>
	)
}
