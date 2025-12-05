import { useValiderIdenter } from '@/utils/hooks/useIdentPool'
import { Alert, Box, Textarea, VStack } from '@navikt/ds-react'
import React, { useEffect, useState } from 'react'
import { FormProvider, useForm } from 'react-hook-form'
import NavButton from '@/components/ui/button/NavButton/NavButton'
import { useCurrentBruker } from '@/utils/hooks/useBruker'
import { AdminAccessDenied } from '@/pages/adminPages/AdminAccessDenied'
import Loading from '@/components/ui/loading/Loading'
import { IdentvalidatorVisningTable } from '@/pages/identvalidator/IdentvalidatorVisningTable'

const initialValues = {
	identer: '',
}

export default () => {
	const [identer, setIdenter] = useState('')
	const { validering, loading, error } = useValiderIdenter(identer)

	const formMethods = useForm({
		mode: 'onChange',
		defaultValues: initialValues,
	})

	useEffect(() => {
		if (validering && !loading && !error) {
			formMethods.reset(initialValues)
		}
	}, [validering])

	const handleValidate = (data: { identer: string }) => {
		setIdenter(data?.identer)
	}

	const { currentBruker, loading: currenBrukerLoading } = useCurrentBruker()
	if (currenBrukerLoading) {
		return <Loading label="Sjekker tilgang ..." />
	}
	if (currentBruker?.brukertype !== 'AZURE') {
		return <AdminAccessDenied />
	}

	return (
		<>
			<h1>Valider identer</h1>
			<VStack gap="space-16">
				<Box background={'surface-default'} padding="6">
					<FormProvider {...formMethods}>
						<form onSubmit={formMethods.handleSubmit(handleValidate)}>
							<Textarea
								name="identer"
								label="Identer"
								value={formMethods.watch('identer')}
								onChange={(event) => formMethods.setValue('identer', event.target.value)}
								description="Skriv inn én eller flere identer, adskilt med mellomrom, komma, semikolon eller linjeskift."
								resize="vertical"
							/>
							<NavButton
								variant={'primary'}
								type={'submit'}
								loading={loading}
								style={{ marginTop: '15px' }}
							>
								Valider
							</NavButton>
						</form>
					</FormProvider>
				</Box>
				{error && (
					<Alert variant={'error'} size={'small'}>
						Feil ved validering: {error.message}
					</Alert>
				)}
				<IdentvalidatorVisningTable identListe={validering} />
			</VStack>
		</>
	)
}
