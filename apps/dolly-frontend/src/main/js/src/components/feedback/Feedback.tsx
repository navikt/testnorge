import * as React from 'react'
import { useState } from 'react'
import { useToggle } from 'react-use'
import { ThumbsRating } from '@/components/rating'
// @ts-ignore
import { v4 as _uuid } from 'uuid'
// @ts-ignore
import dolly from '@/favicon.ico'
import Icon from '@/components/ui/icon/Icon'

import './Feedback.less'
import { useBrukerProfil, useBrukerProfilBilde, useCurrentBruker } from '@/utils/hooks/useBruker'
import { Button, Checkbox, Textarea } from '@navikt/ds-react'
import { Logger } from '@/logger/Logger'

enum Rating {
	Positive = 'POSITIVE',
	Negative = 'NEGATIVE',
}

interface FeedbackProps {
	label: string
	feedbackFor: string
	etterBestilling?: boolean
}

const MAX_LENGTH = 2000

export const Feedback = ({ label, feedbackFor, etterBestilling = false }: FeedbackProps) => {
	const { brukerBilde } = useBrukerProfilBilde()
	const { brukerProfil } = useBrukerProfil()
	const { currentBruker } = useCurrentBruker()

	const [rating, setRating] = useState<Rating>()
	const [text, setText] = useState('')
	const [uuid] = useState(_uuid())
	const [submit, setSubmit] = useState(false)
	const [isAnonym, toggleAnonym] = useToggle(false)

	return (
		<ThumbsRating
			etterBestilling={etterBestilling}
			label={label}
			ratingFor={feedbackFor}
			onClick={(rating) => setRating(rating)}
			uuid={uuid}
		>
			{!submit && (
				<form className="feedback-form">
					<div className="feedback-wrapper">
						{isAnonym ? (
							<Icon kind="user" fontSize={'2rem'} className="bruker-ikon" />
						) : (
							<img alt="Profilbilde" src={brukerBilde || dolly} />
						)}
						<div className="feedback-input">
							<Textarea
								value={text}
								label=""
								placeholder={'(Valgfritt) ' + label}
								maxLength={MAX_LENGTH}
								onChange={(event) => setText(event.target.value)}
								error={text.length > MAX_LENGTH && 'Tilbakemelding inneholder for mange tegn'}
							/>
							{/* @ts-ignore */}
							<Checkbox label="Jeg ønsker å være anonym" onChange={toggleAnonym}>
								Jeg ønsker å være anonym
							</Checkbox>
						</div>
					</div>
					<div className="feedback-form__submit">
						<Button
							disabled={text.length > MAX_LENGTH}
							autoFocus={true}
							onClick={(event) => {
								event.preventDefault()
								Logger.log({
									event: `Tilbakemelding for: ${feedbackFor}`,
									rating: rating,
									message: text,
									uuid: uuid,
									isAnonym: isAnonym,
									brukerType: currentBruker.brukertype,
									brukernavn: isAnonym ? null : brukerProfil?.visningsNavn,
									tilknyttetOrganisasjon: isAnonym ? null : brukerProfil?.organisasjon,
								})
								setSubmit(true)
							}}
						>
							Send
						</Button>
					</div>
				</form>
			)}
		</ThumbsRating>
	)
}
