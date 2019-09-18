import React, { PureComponent, Fragment } from 'react'
import PropTypes from 'prop-types'
import { Formik, Form, Field, getIn } from 'formik'
import Knapp from 'nav-frontend-knapper'
import * as yup from 'yup'
import { FormikDollySelect } from '~/components/fields/Select/Select'
import { FormikInput } from '~/components/fields/Input/Input'
import { DollyApi } from '~/service/Api'
import Loading from '~/components/ui/loading/Loading'
import Table from '~/components/ui/table/Table'

export default class RedigerGruppe extends PureComponent {
	static propTypes = {
		gruppe: PropTypes.shape({
			id: PropTypes.number,
			navn: PropTypes.string,
			teamTilhoerlighetNavn: PropTypes.string,
			hensikt: PropTypes.string
		}),
		teamId: PropTypes.string,
		createTeam: PropTypes.func,
		createGruppe: PropTypes.func,
		updateGruppe: PropTypes.func,
		onCancel: PropTypes.func,
		error: PropTypes.string
	}

	state = { teamToggle: false }

	erRedigering = Boolean(getIn(this.props.gruppe, 'id', false))
	Teams = Object.freeze({ currentUser: -1, newTeam: -2 })

	render() {
		const { currentUserId, gruppe, createOrUpdateFetching, error } = this.props

		if (createOrUpdateFetching) {
			return (
				<Table.Row>
					<Loading label="oppdaterer gruppe" />
				</Table.Row>
			)
		}

		let initialValues = {
			navn: getIn(gruppe, 'navn', ''),
			teamId: getIn(gruppe, 'team.id', null),
			hensikt: getIn(gruppe, 'hensikt', ''),
			teamnavn: getIn(gruppe, 'teamnavn', ''),
			beskrivelse: getIn(gruppe, 'beskrivelse', '')
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
				render={props => {
					const { values, touched, errors, dirty, isSubmitting } = props
					return (
						<Form className="opprett-tabellrad" autoComplete="off">
							<div className="fields">
								<Field name="navn" label="NAVN" autoFocus component={FormikInput} />
								<Field name="hensikt" label="HENSIKT" component={FormikInput} />
								<Field
									name="teamId"
									label="VELG TEAM"
									beforeChange={option => this.onBeforeChange(option)}
									component={FormikDollySelect}
									loadOptions={() =>
										DollyApi.getTeamsByUserId(currentUserId).then(res => this.loadOptions(res))
									}
								/>
								{!this.state.teamToggle && buttons}
							</div>
							{this.state.teamToggle && (
								<div className="fields">
									<Field name="teamnavn" label="team navn" component={FormikInput} />
									<Field name="beskrivelse" label="team beskrivelse" component={FormikInput} />
									{buttons}
								</div>
							)}
							{error && (
								<div className="opprett-error">
									<span>{error.message}</span>
								</div>
							)}
						</Form>
					)
				}}
			/>
		)
	}

	onHandleSubmit = async (values, actions) => {
		const { createGruppe, updateGruppe, gruppe } = this.props

		let groupValues = {
			hensikt: values.hensikt,
			navn: values.navn,
			teamId: await this.getTeam(values)
		}

		return this.erRedigering ? updateGruppe(gruppe.id, groupValues) : createGruppe(groupValues)
	}

	getTeam = async values => {
		const { currentUserId, createTeam } = this.props
		let teamValues = null
		if (values.teamId === this.Teams.newTeam) {
			teamValues = { navn: values.teamnavn, beskrivelse: values.beskrivelse }
		} else if (values.teamId === this.Teams.currentUser) {
			teamValues = { navn: currentUserId, beskrivelse: null }
		}
		if (teamValues != null) {
			await createTeam(teamValues)
			return this.props.teamId
		} else {
			return values.teamId
		}
	}

	onCancel() {
		this.setState({ teamToggle: false })
		this.props.onCancel()
	}

	onBeforeChange(option) {
		const toggle = option != null && option.value === this.Teams.newTeam
		if (toggle !== this.state.teamToggle) {
			this.setState({ teamToggle: toggle })
		}
	}

	loadOptions(res) {
		const { currentUserId } = this.props
		let teams = DollyApi.Utils.NormalizeTeamListForDropdown(res)
		if (!teams.options.some(option => option.label === currentUserId)) {
			teams.options.unshift({
				value: this.Teams.currentUser,
				label: currentUserId + ' (Ditt eget team)'
			})
		} else {
			let index = teams.options.findIndex(option => option.label === currentUserId)
			teams.options.splice(0, 0, teams.options.splice(index, 1)[0])
			teams.options[0].label += ' (Ditt eget team)'
		}
		teams.options.push({
			value: this.Teams.newTeam,
			label: '+ Opprett nytt team'
		})
		return teams
	}

	validation = () =>
		yup.object().shape({
			navn: yup
				.string()
				.trim()
				.required('Navn er et påkrevd felt')
				.max(30, 'Maksimalt 30 bokstaver'),
			teamId: yup
				.number()
				.required('Team er et påkrevd felt')
				.nullable(),
			hensikt: yup // .required('Du må velge hvilket team gruppen skal knyttes til'),
				.string()
				.trim()
				.required('Gi en liten beskrivelse av hensikten med gruppen')
				.max(200, 'Maksimalt 200 bokstaver'),
			teamnavn: yup.string().when('teamId', {
				is: val => val === this.Teams.newTeam,
				then: yup
					.string()
					.trim()
					.required('Teamnavn er et påkrevd felt')
					.max(30, 'Maksimalt 30 bokstaver')
			}),
			beskrivelse: yup.string().when('teamId', {
				is: val => val === this.Teams.newTeam,
				then: yup
					.string()
					.trim()
					.required('Gi en liten beskrivelse av teamet')
					.max(200, 'Maksimalt 200 bokstaver')
			})
		})
}
