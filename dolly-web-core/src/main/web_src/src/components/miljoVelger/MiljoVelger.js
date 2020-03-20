import React from 'react'
import { useSelector } from 'react-redux'
import { FieldArray, ErrorMessage } from 'formik'
import LinkButton from '~/components/ui/button/LinkButton/LinkButton'
import { DollyCheckbox } from '~/components/ui/form/inputs/checbox/Checkbox'
import { MiljoeInfo } from './MiljoeInfo/MiljoeInfo'

import './MiljoVelger.less'

export const MiljoVelger = ({ bestillingsdata, heading }) => {
	const environments = useSelector(state => state.environments.data)
	console.log('environments :', environments)
	if (!environments) return null

	const order = ['U', 'T', 'Q']

	return (
		<div className="miljo-velger">
			<h2>{heading}</h2>
			<MiljoeInfo bestillingsdata={bestillingsdata} dollyEnvironments={environments} />

			<FieldArray name="environments">
				{({ push, remove, form }) => {
					const values = form.values.environments

					const isChecked = id => values.includes(id)

					const onClick = e => {
						const { id } = e.target
						isChecked(id) ? remove(values.indexOf(id)) : push(id)
					}

					const velgAlle = type => {
						console.log('type :', type)
						const c = environments[type].filter(f => !isChecked(f.id)).map(a => a.id)
						const n = values.concat(c)
						form.setFieldValue('environments', n)
					}

					const fjernAlle = type => {
						form.setFieldValue(
							'environments',
							values.filter(id => !environments[type].map(a => a.id).includes(id))
						)
					}

					const formatereEnvs = category => {
						const sortereNummer = category.map(env => env.id.match(/.{1,1}/g))
						const sortereEnvs = []

						sortereNummer.map((v, idx) => {
							sortereEnvs.push(sortereNummer[idx][0] + sortereNummer[idx][1])
						})
						const sorterteEnvs = sortereEnvs.sort()
						console.log('sorterteEnvs :', sorterteEnvs)
						console.log('sortereEnvs :', sortereEnvs)
					}

					return order.map(type => {
						const category = environments[type]
						const allDisabled = category.some(f => f.disabled)
						console.log('category :', category)

						return (
							<fieldset key={type} name={`Liste over ${type}-mijøer`}>
								<h3>{type}-miljø</h3>
								<div className="miljo-velger_checkboxes">
									{category.map(env => (
										<DollyCheckbox
											key={env.id}
											id={env.id}
											disabled={env.disabled}
											label={env.label}
											checked={values.includes(env.id)}
											onClick={onClick}
											onChange={() => {}}
											size={'xxsmall'}
										/>
									))}
								</div>
								{!allDisabled && (
									<div className="miljo-velger_buttons">
										<LinkButton text="Velg alle" onClick={e => velgAlle(type)} />
										<LinkButton text="Fjern alle" onClick={e => fjernAlle(type)} />
										<LinkButton text="sortenvs" onClick={e => formatereEnvs(category)} />
									</div>
								)}
							</fieldset>
						)
					})
				}}
			</FieldArray>

			<ErrorMessage name="environments" className="error-message" component="div" />
		</div>
	)
}
