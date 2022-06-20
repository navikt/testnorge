import * as React from 'react'
import { useState } from 'react'
import { useToggle } from 'react-use'
import { ThumbsRating } from '../rating'
import { Rating } from '~/logger/types'
import Logger from '../../logger'
import { DollyCheckbox } from '~/components/ui/form/inputs/checbox/Checkbox'
// @ts-ignore
import { v4 as _uuid } from 'uuid'
// @ts-ignore
import dolly from '~/assets/favicon.ico'
import Icon from '~/components/ui/icon/Icon'

import './Feedback.less'
import { useBrukerProfilBilde } from '~/utils/hooks/useBruker'
import { Button, Textarea } from '@navikt/ds-react'

interface FeedbackProps {
	label: string
	feedbackFor: string
}

const MAX_LENGTH = 2000

export const Feedback = ({ label, feedbackFor }: FeedbackProps) => {
	const { brukerBilde } = useBrukerProfilBilde()

	const [rating, setRating] = useState<Rating>()
	const [text, setText] = useState('')
	const [uuid] = useState(_uuid())
	const [submit, setSubmit] = useState(false)
	const [isAnonym, toggleAnonym] = useToggle(false)

	return (
		<ThumbsRating
			label={label}
			ratingFor={feedbackFor}
			onClick={(rating) => setRating(rating)}
			uuid={uuid}
		>
			{!submit && (
				<form className="feedback-form">
					<div className="feedback-wrapper">
						{isAnonym ? (
							<Icon kind="user" size={40} className="bruker-ikon" />
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
								error={text.length > MAX_LENGTH ? 'Tilbakemelding inneholder for mange tegn' : null}
							/>
							{/* @ts-ignore */}
							<DollyCheckbox label="Jeg ønsker å være anonym" onChange={toggleAnonym} />
						</div>
					</div>
					<div className="feedback-form__submit">
						<Button
							type="submit"
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
