import React from 'react'
import cn from 'classnames'
import './ApiFeilmelding.less'

export default function ApiFeilmelding({ feilmelding, container }) {
	if (!feilmelding || feilmelding === 'OK') return false
	const feilmeldingSetninger = feilmelding.split(' ')
	const formatertFeilmelding = feilmeldingSetninger.map((setning) => {
		console.log('setning: ', setning) //TODO - SLETT MEG
		console.log('setning.length: ', setning.length) //TODO - SLETT MEG
		if (setning.length < 60) {
			return setning
		} else {
			return 'Too long'
		}
	})
	console.log('form: ', formatertFeilmelding) //TODO - SLETT MEG
	const css = cn('api-feilmelding', {
		'api-feilmelding-container': container,
	})
	return <pre className={css}>{feilmelding}</pre>
}
