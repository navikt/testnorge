import React, { Fragment, PureComponent } from 'react'
import PropTypes from 'prop-types'
import { Form, Formik, getIn } from 'formik'
import NavButton from '~/components/ui/button/NavButton/NavButton'
import * as yup from 'yup'
import Loading from '~/components/ui/loading/Loading'
import { FormikTextInput } from '~/components/ui/form/inputs/textInput/TextInput'

import './RedigerGruppe.less'

export default class RedigerGruppe extends PureComponent {
	static propTypes = {
		gruppe: PropTypes.shape({
			id: PropTypes.number,
			navn: PropTypes.string,
			hensikt: PropTypes.string,
		}),
		createGruppe: PropTypes.func,
		updateGruppe: PropTypes.func,
		onCancel: PropTypes.func,
		error: PropTypes.string,
	}

	erRedigering = Boolean(getIn(this.props.gruppe, 'id', false))

	render() {
		const { gruppe, createOrUpdateFetching, error } = this.props

		if (createOrUpdateFetching) {
			return <Loading label="oppdaterer gruppe" />
		}

		const initialValues = {
			navn: getIn(gruppe, 'navn', ''),
			hensikt: getIn(gruppe, 'hensikt', ''),
		}

		const buttons = (
			<Fragment>
				<NavButton htmlType={'reset'} type={'fare'} onClick={() => this.onCancel()}>
					Avbryt
				</NavButton>
				<NavButton type="hoved" htmlType="submit">
					{this.erRedigering ? 'Lagre' : 'Opprett og gå til gruppe'}
				</NavButton>
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
							<FormikTextInput name="hensikt" label="HENSIKT" size="grow" useOnChange={true} />
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

	onHandleSubmit = async (values, _actions) => {
		const { createGruppe, updateGruppe, gruppe, error } = this.props

		const groupValues = {
			hensikt: values.hensikt,
			navn: values.navn,
		}
		this.erRedigering
			? await updateGruppe(gruppe.id, groupValues)
			: await createGruppe(groupValues).then((response) => {
					const gruppeId = response.value?.data?.id
					if (gruppeId) window.location.href = `/gruppe/${gruppeId}`
			  })
		return !error && this.onCancel()
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
				.max(200, 'Maksimalt 200 bokstaver'),
		})
}
