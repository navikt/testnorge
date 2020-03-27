import * as React from 'react'
import { useState } from 'react'
import { ThumbsRating } from '../rating'
import { Rating } from '../../logger/types'
import Logger from '../../logger'
import { Textarea } from 'nav-frontend-skjema'
import { Knapp } from 'nav-frontend-knapper'
// @ts-ignore
import { v4 as _uuid } from 'uuid'

import './Feedback.less'

interface FeedbackProps {
	label: string
	feedbackFor: string
}

const MAX_LENGTH = 2000

export const Feedback = ({ label, feedbackFor }: FeedbackProps) => {
	const [rating, setRating] = useState<Rating>()
	const [text, setText] = useState('')
	const [uuid] = useState(_uuid())
	const [submit, setSubmit] = useState(false)

	return (
		<ThumbsRating
			label={label}
			ratingFor={feedbackFor}
			onClick={rating => setRating(rating)}
			uuid={uuid}
		>
			{!submit && (
				<form className="feedback-form">
					<Textarea
						value={text}
						label=""
						placeholder={label}
						maxLength={MAX_LENGTH}
						onChange={event => setText(event.target.value)}
						feil={
							text.length > MAX_LENGTH
								? { feilmelding: 'Tilbakemelding inneholder for mange tegn' }
								: null
						}
					/>
					<div className="feedback-form__submit">
						<Knapp
							form="kompakt"
							htmlType="submit"
							disabled={text.length < 1 || text.length > MAX_LENGTH}
							autoFocus={true}
							onClick={event => {
								event.preventDefault()
								Logger.log({
									event: `Tilbakemelding for: ${feedbackFor}`,
									rating: rating,
									message: text,
									uuid: uuid
								})
								setSubmit(true)
							}}
						>
							Send
						</Knapp>
					</div>
				</form>
			)}
		</ThumbsRating>
	)
}
