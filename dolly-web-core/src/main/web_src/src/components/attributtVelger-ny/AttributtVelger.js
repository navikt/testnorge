import React from 'react'
import { getAttributter } from '~/service/Attributter'
import { AttributtPaneler } from './AttributtPaneler'
import { Utvalg } from './utvalg/Utvalg'

import './AttributtVelger.less'

export const FormikAttributtVelger = ({ formikBag }) => {
	const attrs = formikBag.values.attributter

	const setValue = (key, bool) => {
		if (attrs[key] !== bool) formikBag.setFieldValue(`attributter.${key}`, bool)
	}

	const checkAttributter = (list, bool = false) => {
		if (!list || !Array.isArray(list)) return false
		list.forEach(key => setValue(key, bool))
	}

	return <AttributtVelger valgteAttributter={attrs} checkAttributter={checkAttributter} />
}

const AttributtVelger = ({ valgteAttributter, checkAttributter }) => {
	const attributter = getAttributter()
	return (
		<div className="attributt-velger">
			<div className="flexbox">
				<AttributtPaneler attributter={attributter} checkAttributter={checkAttributter} />
				<Utvalg valgteAttributter={valgteAttributter} checkAttributter={checkAttributter} />
			</div>
		</div>
	)
}
