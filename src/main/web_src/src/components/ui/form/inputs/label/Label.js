import React from 'react'
import cn from 'classnames'
import _isNil from 'lodash/isNil'
import _isEmpty from 'lodash/isEmpty'

export const Label = ({ name, label, feil, containerClass, children }) => {
	const wrapClass = cn('skjemaelement', containerClass, {
		error: Boolean(feil && !_isEmpty(feil.feilmelding)),
		'label-offscreen': _isNil(label)
	})
	return (
		<div className={wrapClass}>
			<label htmlFor={name} className="skjemaelement__label">
				{label}
			</label>
			{children}
			{feil && !_isEmpty(feil.feilmelding) && (
				<div role="alert" aria-live="assertive">
					<div className="skjemaelement__feilmelding">{feil.feilmelding}</div>
				</div>
			)}
		</div>
	)
}
