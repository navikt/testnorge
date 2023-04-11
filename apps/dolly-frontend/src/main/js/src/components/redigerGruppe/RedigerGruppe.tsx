import React from 'react'
import { Form, Formik, getIn } from 'formik'
import NavButton from '@/components/ui/button/NavButton/NavButton'
import * as yup from 'yup'
import Loading from '@/components/ui/loading/Loading'
import { FormikTextInput } from '@/components/ui/form/inputs/textInput/TextInput'

import './RedigerGruppe.less'
import { useNavigate } from 'react-router-dom'
import { REGEX_BACKEND_GRUPPER, useMatchMutate } from '@/utils/hooks/useMutate'

type Props = {
	gruppe: {
		id: number
		navn: string
		hensikt: string
	}
	createGruppe: (arg0: any) => any
	createOrUpdateFetching: boolean
	updateGruppe: (arg0: number, arg1: any) => any
	onCancel: () => void
	error: {
		message: string
	}
}
const RedigerGruppe = ({
	gruppe,
	createGruppe,
	createOrUpdateFetching,
	updateGruppe,
	onCancel,
	error,
}: Props): JSX.Element => {
	const navigate = useNavigate()
	const erRedigering = Boolean(getIn(gruppe, 'id', false))
	const mutate = useMatchMutate()

	const onHandleSubmit = async (values: { hensikt: any; navn: any }, _actions: any) => {
		const groupValues = {
			hensikt: values.hensikt,
			navn: values.navn,
		}
		erRedigering
			? await updateGruppe(gruppe.id, groupValues).then(() => {
					return mutate(REGEX_BACKEND_GRUPPER)
			  })
			: await createGruppe(groupValues).then((response: { value: { data: { id: any } } }) => {
					const gruppeId = response.value?.data?.id
					if (gruppeId) {
						navigate(`/gruppe/${gruppeId}`)
					}
			  })
		return !error && onCancel()
	}

	const validation = () =>
		yup.object().shape({
			navn: yup
				.string()
				.trim()
				.required('Navn er et påkrevd felt')
				.max(30, 'Maksimalt 30 bokstaver'),
			hensikt: yup
				.string()
				.trim()
				.required('Gi en beskrivelse av hensikten med gruppen')
				.max(200, 'Maksimalt 200 bokstaver'),
		})

	if (createOrUpdateFetching) {
		return <Loading label="oppdaterer gruppe" />
	}

	const initialValues = {
		navn: getIn(gruppe, 'navn', ''),
		hensikt: getIn(gruppe, 'hensikt', ''),
	}

	const buttons = (
		<>
			<NavButton data-cy={'button_opprett'} variant={'primary'} type={'submit'}>
				{erRedigering ? 'Lagre' : 'Opprett og gå til gruppe'}
			</NavButton>
			<NavButton type={'reset'} variant={'danger'} onClick={() => onCancel()}>
				Avbryt
			</NavButton>
		</>
	)

	return (
		<Formik initialValues={initialValues} validationSchema={validation} onSubmit={onHandleSubmit}>
			{() => (
				<Form className="opprett-tabellrad" autoComplete="off">
					<div className="fields">
						<FormikTextInput
							data-cy={'input_navn'}
							name="navn"
							label="NAVN"
							size="grow"
							useOnChange={true}
							autoFocus
						/>
						<FormikTextInput
							data-cy={'input_hensikt'}
							name="hensikt"
							label="HENSIKT"
							size="grow"
							useOnChange={true}
						/>
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
export default RedigerGruppe
