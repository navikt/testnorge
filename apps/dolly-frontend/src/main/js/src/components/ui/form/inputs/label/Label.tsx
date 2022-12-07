import cn from 'classnames'
import * as _ from 'lodash-es'
import styled from 'styled-components'
import './Label.less'
import { Hjelpetekst } from '@/components/hjelpetekst/Hjelpetekst'

const StyledLabel = styled.label`
	font-size: 0.75em;
	text-transform: uppercase;
`

export const Label = ({ name, label, feil, info = null, containerClass, children }) => {
	const wrapClass = cn('skjemaelement', containerClass, {
		error: Boolean(feil && !_.isEmpty(feil.feilmelding)),
		'label-offscreen': _.isNil(label),
	})

	return (
		<div className={wrapClass}>
			{info ? (
				<div className="flexbox--align-center--justify-start">
					<StyledLabel htmlFor={name} className="skjemaelement__label">
						{label}
					</StyledLabel>
					<div className="label-hjelpetekst">
						<Hjelpetekst>{info}</Hjelpetekst>
					</div>
				</div>
			) : (
				<StyledLabel htmlFor={name} className="skjemaelement__label">
					{label}
				</StyledLabel>
			)}
			{children}
			{feil && !_.isEmpty(feil.feilmelding) && (
				<div role="alert" aria-live="assertive">
					<div className="skjemaelement__feilmelding">{feil.feilmelding}</div>
				</div>
			)}
		</div>
	)
}
