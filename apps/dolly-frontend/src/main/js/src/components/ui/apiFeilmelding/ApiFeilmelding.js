import React from 'react'
import cn from 'classnames'
import './ApiFeilmelding.less'

function formaterFeilmelding(feilmeldingSetninger) {
	return feilmeldingSetninger.map((setning) => {
		if (setning.length < 60) {
			return setning.concat(' ')
		} else {
			return setning.replaceAll('/', ' /').concat(' ')
		}
	})
}

export default function ApiFeilmelding({ feilmelding, container }) {
	if (!feilmelding || feilmelding === 'OK') return false
	const formatertFeilmelding = formaterFeilmelding(feilmelding.split(' '))
	const css = cn('api-feilmelding', {
		'api-feilmelding-container': container,
	})
	return <pre className={css}>{formatertFeilmelding}</pre>
}
