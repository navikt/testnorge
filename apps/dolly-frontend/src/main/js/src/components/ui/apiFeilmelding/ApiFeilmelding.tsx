import React from 'react'
import cn from 'classnames'
import './ApiFeilmelding.less'

type ApiFeilmeldingProps = {
	feilmelding: string
	container?: boolean
}

function formaterFeilmelding(feilmeldingSetninger: string[]) {
	return feilmeldingSetninger.map((setning) => {
		if (setning.length < 60) {
			return setning.concat(' ')
		} else {
			return setning.replaceAll('/', ' /').concat(' ')
		}
	})
}

export default function ApiFeilmelding({ feilmelding, container }: ApiFeilmeldingProps) {
	if (!feilmelding || feilmelding === 'OK') return null
	const formatertFeilmelding = formaterFeilmelding(feilmelding.split(' '))
	const css = cn('api-feilmelding', {
		'api-feilmelding-container': container,
	})
	return <pre className={css}>{formatertFeilmelding}</pre>
}
