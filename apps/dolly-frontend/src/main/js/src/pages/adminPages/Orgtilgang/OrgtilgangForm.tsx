import { Alert, Box, Button } from '@navikt/ds-react'
import { Form, FormProvider, useForm } from 'react-hook-form'
import { FormTextInput } from '@/components/ui/form/inputs/textInput/TextInput'
import { FormSelect } from '@/components/ui/form/inputs/select/Select'
import OrganisasjonTilgangService from '@/service/services/organisasjonTilgang/OrganisasjonTilgangService'
import { arrayToString } from '@/utils/DataFormatter'
import { useState } from 'react'
import { useBoolean } from 'react-use'
import Loading from '@/components/ui/loading/Loading'

type Inputs = {
	organisasjonsnummer: string
	miljoe: string
}

type Response = {
	data: OrgtilgangTypes
}

export type OrgtilgangTypes = {
	organisasjonsnummer: string
	miljoe: string
	navn: string
	organisasjonsform: string
}

const initialValues = {
	organisasjonsnummer: '',
	miljoe: [],
}

export const miljoer = [
	{ label: 'Q1', value: 'q1' },
	{ label: 'Q2', value: 'q2' },
]

export const OrgtilgangForm = ({ mutate }: any) => {
	const formMethods = useForm({
		mode: 'onChange',
		defaultValues: initialValues,
	})

	const { handleSubmit, reset, control } = formMethods

	const [isLoading, setIsLoading] = useBoolean(false)
	const [okStatus, setOkStatus] = useState('')
	const [error, setError] = useState('')

	const onSubmit = async (values: Inputs) => {
		setIsLoading(true)
		setOkStatus('')
		setError('')
		reset()
		const formattedValues = { ...values, miljoe: arrayToString(values.miljoe).replaceAll(' ', '') }
		await OrganisasjonTilgangService.postOrganisasjon(formattedValues)
			.then((response: Response) => {
				setIsLoading(false)
				setOkStatus(`Tilgang opprettet for ${response?.data?.navn}`)
				mutate()
			})
			.catch((error: any) => {
				setIsLoading(false)
				setError(`Feil: ${error?.message}`)
			})
	}

	return (
		<Box background="default" padding="space-16" style={{ marginBottom: '15px' }}>
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
					</div>
					<div className="flexbox--align-center">
						<Button type="submit" variant="primary" style={{ marginRight: '15px' }}>
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
