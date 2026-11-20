import { useValiderIdenter } from '@/utils/hooks/useIdentPool'
import { Box, InfoCard, Textarea, VStack } from '@navikt/ds-react'
import { XMarkOctagonFillIcon } from '@navikt/aksel-icons'
import React, { useEffect } from 'react'
import { FormProvider, useForm } from 'react-hook-form'
import NavButton from '@/components/ui/button/NavButton/NavButton'
import { useCurrentBruker } from '@/utils/hooks/useBruker'
import { AdminAccessDenied } from '@/pages/adminPages/AdminAccessDenied'
import Loading from '@/components/ui/loading/Loading'
import { IdentvalidatorVisningTable } from '@/pages/identvalidator/IdentvalidatorVisningTable'
import Button from '@/components/ui/button/Button'
import { jsonToCsvDownload } from '@/pages/identvalidator/utils'

export default () => {
	const { validering, loading, error, trigger, reset } = useValiderIdenter()
	const formMethods = useForm({ mode: 'onChange', defaultValues: { identer: '' } })

	useEffect(() => {
		if (validering && !loading && !error) {
			formMethods.reset({ identer: '' })
		}
	}, [validering, loading, error])

	const handleValidate = async (data: { identer: string }) => {
		reset()
		await trigger(data.identer)
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
				<Box background={'default'} padding="space-16">
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
							<div className="flexbox--align-center" style={{ marginTop: '15px' }}>
								<NavButton variant={'primary'} type={'submit'} loading={loading}>
									Valider
								</NavButton>
								{validering?.length > 0 && (
									<Button
										style={{ marginLeft: '20px' }}
										kind="download"
										onClick={() => jsonToCsvDownload(validering, 'identvalidering.csv')}
									>
										LAST NED CSV-FIL
									</Button>
								)}
							</div>
						</form>
					</FormProvider>
				</Box>
				{error && (
					<InfoCard data-color="danger" size={'small'}>
						<InfoCard.Message icon={<XMarkOctagonFillIcon aria-hidden />}>
							Feil ved validering: {error.message}
						</InfoCard.Message>
					</InfoCard>
				)}
				<IdentvalidatorVisningTable identListe={validering} />
			</VStack>
		</>
	)
}
