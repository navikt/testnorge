import React, { Component } from 'react'
import PropTypes from 'prop-types'
import Panel from '~/components/panel/Panel'
import Checkbox from '~/components/fields/Checkbox/Checkbox'

import './MiljoVelger.less'
import { array } from 'yup'

const generateEnv = (label, start, length) => {
	const envList = []

	for (let i = start; i <= length; i++) {
		envList.push(`${label + i}`)
	}
	return envList
}

class MiljoVelger extends Component {
	static propTypes = {
		heading: PropTypes.string
	}

	onClickHandler = e => {
		const { arrayHelpers, arrayValues } = this.props
		const indexOf = arrayValues.indexOf(e.target.id)

		if (e.target.checked && indexOf === -1) return arrayHelpers.push(e.target.id)

		return arrayHelpers.remove(indexOf)
	}

	createCheckbox = id => (
		<Checkbox
			key={id}
			id={id}
			disabled={id !== 'u6'} // TEMP: Kun bruk dette miljøet foreløpig
			label={id}
			checked={this.props.arrayValues.includes(id)}
			onClick={this.onClickHandler}
		/>
	)

	render() {
		const { heading, arrayHelpers } = this.props

		const UList = generateEnv('u', 6, 6)
		const TList = generateEnv('t', 0, 13)
		const QList = generateEnv('q', 0, 11)

		return (
			<div className="miljo-velger">
				<h3>{heading}</h3>
				<div className="miljo-velger_content">
					<div>{UList.map(env => this.createCheckbox(env))}</div>
					<div>{TList.map(env => this.createCheckbox(env))}</div>
					<div>{QList.map(env => this.createCheckbox(env))}</div>
				</div>
			</div>
		)
	}
}

export default MiljoVelger
