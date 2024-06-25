import { Alert, Box, Button } from '@navikt/ds-react'
import { Form, FormProvider, useForm } from 'react-hook-form'
import { FormTextInput } from '@/components/ui/form/inputs/textInput/TextInput'
import { FormDatepicker } from '@/components/ui/form/inputs/datepicker/Datepicker'
import { FormSelect } from '@/components/ui/form/inputs/select/Select'
import OrganisasjonTilgangService from '@/service/services/organisasjonTilgang/OrganisasjonTilgangService'
import { arrayToString } from '@/utils/DataFormatter'
import { useState } from 'react'
import { useBoolean } from 'react-use'
import Loading from '@/components/ui/loading/Loading'
import * as Yup from 'yup'
import { requiredString } from '@/utils/YupValidations'
import { yupResolver } from '@hookform/resolvers/yup'

type Inputs = {
	organisasjonsnummer: string
	gyldigTil: string
	miljoe: string
}

//TODO: Er det mulig å få denne til å funke?
const validation = Yup.object({
	organisasjonsnummer: Yup.string().required('Organisasjonsnummer er påkrevd'),
	gyldigTil: Yup.string().required('Dato er påkrevd'),
	miljoe: Yup.array().min(1, 'Velg minst ett miljø').required(),
})

const initialValues = {
	organisasjonsnummer: '',
	gyldigTil: '',
	miljoe: [],
}

const miljoer = [
	{ label: 'Q1', value: 'q1' },
	{ label: 'Q2', value: 'q2' },
]

export const OrgtilgangForm = ({ mutate }) => {
	const formMethods = useForm({
		mode: 'onChange',
		defaultValues: initialValues,
		resolver: yupResolver(validation),
	})
	const { trigger, watch, handleSubmit, reset, control, setValue } = formMethods

	const [isLoading, setIsLoading] = useBoolean(false)
	const [okStatus, setOkStatus] = useState('')
	const [error, setError] = useState('')

	const onSubmit = async (values) => {
		trigger()
		setIsLoading(true)
		setOkStatus('')
		setError('')
		reset()
		const formattedValues = { ...values, miljoe: arrayToString(values.miljoe).replaceAll(' ', '') }
		await OrganisasjonTilgangService.postOrganisasjon(formattedValues)
			.then((response) => {
				setIsLoading(false)
				setOkStatus(`Tilgang opprettet for ${response?.data?.navn}`)
				mutate()
			})
			.catch((error) => {
				setIsLoading(false)
				setError(`Feil: ${error?.message}`)
			})
	}
	console.log('watch(): ', watch()) //TODO - SLETT MEG
	return (
		<Box background="surface-default" padding="4" style={{ marginBottom: '15px' }}>
			<h2 style={{ marginTop: '5px' }}>Opprett tilgang</h2>
			<FormProvider {...formMethods}>
				<Form control={control} autoComplete={'off'} onSubmit={handleSubmit(onSubmit)}>
					<div className="flexbox--flex-wrap">
						<FormTextInput
							name={'organisasjonsnummer'}
							label={'Organisasjonsnummer'}
							size="large"
						/>
						<FormSelect
							name={'miljoe'}
							label={'Miljø'}
							options={miljoer}
							isMulti={true}
							size="medium"
						/>
						<FormDatepicker name={'gyldigTil'} label={'Gyldig til'} />
					</div>
					<div className="flexbox--align-center">
						<Button
							type="submit"
							variant="primary"
							style={{ marginRight: '15px' }}
							onClick={() => trigger()}
						>
							Opprett
						</Button>
						{isLoading && <Loading label="Oppretter tilgang ..." />}
						{okStatus && (
							<Alert variant={'success'} size={'small'} inline>
								{okStatus}
							</Alert>
						)}
						{error && (
							<Alert variant={'error'} size={'small'} inline>
								{error}
							</Alert>
						)}
					</div>
				</Form>
			</FormProvider>
		</Box>
	)
}
