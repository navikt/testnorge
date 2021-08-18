import React, { useState } from 'react'
import { Textarea } from 'nav-frontend-skjema'
import Button from '~/components/ui/button/Button'
import useBoolean from '~/utils/hooks/useBoolean'

import './TextEditor.less'

export const TextEditor = ({ text, handleSubmit, placeholder, maxLength = 1000 }) => {
	const [isEditing, turnOnEditing, turnOffEditing] = useBoolean(false)
	const [value, setValue] = useState(text || '')

	const submit = () => {
		handleSubmit(value)
		turnOffEditing()
	}
	const handleChange = e => setValue(e.target.value)

	const focusEndOfStringHack = e => {
		e.target.value = ''
		e.target.value = value
	}

	return (
		<div className="text-editor">
			{!isEditing && (
				<p className="text-editor-view" onClick={turnOnEditing}>
					{text || <span>{placeholder} (maks 1000 tegn)</span>}
				</p>
			)}

			{isEditing && (
				<Textarea
					label=""
					placeholder={placeholder}
					autoFocus
					onFocus={focusEndOfStringHack}
					value={value}
					onChange={handleChange}
					maxLength={maxLength}
				/>
			)}

			<div className="text-editor-buttons">
				{isEditing && (
					<React.Fragment>
						<Button onClick={turnOffEditing}>Avbryt</Button>
						<Button onClick={submit}>Lagre</Button>
					</React.Fragment>
				)}

				{!isEditing && <Button onClick={turnOnEditing}>Rediger</Button>}
			</div>
		</div>
	)
}
