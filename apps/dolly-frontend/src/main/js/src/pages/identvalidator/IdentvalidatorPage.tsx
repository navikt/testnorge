import { useValiderIdent } from '@/utils/hooks/useIdentPool'
import { Alert, Box, VStack } from '@navikt/ds-react'
import React, { useState } from 'react'
import { FormProvider, useForm } from 'react-hook-form'
import { DollyTextInput } from '@/components/ui/form/inputs/textInput/TextInput'
import NavButton from '@/components/ui/button/NavButton/NavButton'
import { IdentvalidatorVisning } from '@/pages/identvalidator/IdentvalidatorVisning'
import styled from 'styled-components'

const initialValues = {
	ident: '',
}

const StyledForm = styled.div`
	display: flex;

	&& {
		.dolly-form-input {
			height: 48px;
		}
	}
	&&& {
		.skjemaelement__input {
			height: 48px;
		}
	}
`

export default () => {
	const [ident, setIdent] = useState('')
	const { validering, loading, error } = useValiderIdent(ident)

	const formMethods = useForm({
		mode: 'onChange',
		defaultValues: initialValues,
	})

	const handleValidate = (data: { ident: string }) => {
		setIdent(data?.ident)
		formMethods.reset(initialValues)
		//TODO: Vente med å tømme feltet til validering er ferdig?
	}

	return (
		<>
			<h1>Valider ident</h1>
			<p>
				Her kan du validere både nye og gamle test-identer. Skriv inn en ident i feltet nedenfor for
				å vise info om denne identen.
			</p>
			<VStack gap="space-16">
				<Box background={'surface-default'} padding="6">
					<FormProvider {...formMethods}>
						<form onSubmit={formMethods.handleSubmit(handleValidate)}>
							<StyledForm>
								<DollyTextInput name="ident" size="large" />
								<NavButton variant={'primary'} type={'submit'} loading={loading}>
									Valider
								</NavButton>
							</StyledForm>
						</form>
					</FormProvider>
				</Box>
				{error && (
					<Alert variant={'error'} size={'small'}>
						Feil ved validering: {error.message}
					</Alert>
				)}
				<IdentvalidatorVisning data={validering} />
			</VStack>
		</>
	)
}
