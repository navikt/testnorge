import React, { PureComponent, Fragment } from 'react'
import PropTypes from 'prop-types'
import { Formik, Form, getIn } from 'formik'
import Knapp from 'nav-frontend-knapper'
import * as yup from 'yup'
import Loading from '~/components/ui/loading/Loading'
import { FormikTextInput } from '~/components/ui/form/inputs/textInput/TextInput'

export default class RedigerGruppe extends PureComponent {
	static propTypes = {
		gruppe: PropTypes.shape({
			id: PropTypes.number,
			navn: PropTypes.string,
			hensikt: PropTypes.string
		}),
		createGruppe: PropTypes.func,
		updateGruppe: PropTypes.func,
		onCancel: PropTypes.func,
		error: PropTypes.string
	}

	erRedigering = Boolean(getIn(this.props.gruppe, 'id', false))

	render() {
		const { gruppe, createOrUpdateFetching, error } = this.props

		if (createOrUpdateFetching) {
			return <Loading label="oppdaterer gruppe" />
		}

		let initialValues = {
			navn: getIn(gruppe, 'navn', ''),
			hensikt: getIn(gruppe, 'hensikt', '')
		}

		let buttons = (
			<Fragment>
				<Knapp mini type="standard" htmlType="button" onClick={() => this.onCancel()}>
					Avbryt
				</Knapp>
				<Knapp mini type="hoved" htmlType="submit">
					{this.erRedigering ? 'Lagre' : 'Opprett og gå til gruppe'}
				</Knapp>
			</Fragment>
		)

		return (
			<Formik
				initialValues={initialValues}
				validationSchema={this.validation}
				onSubmit={this.onHandleSubmit}
			>
				{() => (
					<Form className="opprett-tabellrad" autoComplete="off">
						<div className="fields">
							<FormikTextInput name="navn" label="NAVN" size="grow" autoFocus />
							<FormikTextInput name="hensikt" label="HENSIKT" size="grow" />
							{buttons}
						</div>
						{error && (
							<div className="opprett-error">
								<span>{error.message}</span>
							</div>
						)}
					</Form>
				)}
			</Formik>
		)
	}

	onHandleSubmit = async (values, actions) => {
		const { createGruppe, updateGruppe, gruppe } = this.props

		let groupValues = {
			hensikt: values.hensikt,
			navn: values.navn
		}
		this.erRedigering ? await updateGruppe(gruppe.id, groupValues) : await createGruppe(groupValues)
		return this.onCancel()
	}

	onCancel() {
		this.props.onCancel()
	}

	validation = () =>
		yup.object().shape({
			navn: yup
				.string()
				.trim()
				.required('Navn er et påkrevd felt')
				.max(30, 'Maksimalt 30 bokstaver'),
			hensikt: yup
				.string()
				.trim()
				.required('Gi en liten beskrivelse av hensikten med gruppen')
				.max(200, 'Maksimalt 200 bokstaver')
		})
}
