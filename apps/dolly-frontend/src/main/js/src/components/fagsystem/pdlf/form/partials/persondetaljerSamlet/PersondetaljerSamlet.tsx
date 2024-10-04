import { NavnForm } from '@/components/fagsystem/pdlf/form/partials/navn/Navn'
import { KjoennForm } from '@/components/fagsystem/pdlf/form/partials/kjoenn/Kjoenn'
import { PersonstatusForm } from '@/components/fagsystem/pdlf/form/partials/personstatus/Personstatus'
import { Alert } from '@navikt/ds-react'
import { Hjelpetekst } from '@/components/hjelpetekst/Hjelpetekst'
import { UseFormReturn } from 'react-hook-form/dist/types'

type PersondetaljerSamletTypes = {
	formMethods: UseFormReturn
	tpsMessaging?: {
		tpsMessagingData: {
			sprakKode: string
		}
	}
	identtype?: string
	harSkjerming?: boolean
	identMaster?: string
}

export const PersondetaljerSamlet = ({
	formMethods,
	tpsMessaging,
	identtype,
	identMaster,
}: PersondetaljerSamletTypes) => {
	const sprak = tpsMessaging?.tpsMessagingData?.sprakKode

	const getTekst = () => {
		if (sprak) {
			return ' og språk'
		} else {
			return ''
		}
	}

	return (
		<>
			<div className="flexbox--full-width">
				<Alert
					variant={'info'}
					size={'small'}
				>{`Identnummer${getTekst()} kan ikke endres her.`}</Alert>

				{formMethods.watch('navn') && (
					<>
						<h3>Navn</h3>
						<div className="flexbox--flex-wrap">
							<NavnForm formMethods={formMethods} path="navn[0]" identtype={identtype} />
						</div>
					</>
				)}

				<h3>Kjønn</h3>
				<KjoennForm
					path="kjoenn[0]"
					kanVelgeMaster={identMaster === 'PDL' || identtype === 'NPID'}
				/>

				{identtype !== 'NPID' && formMethods.watch('folkeregisterpersonstatus')?.length < 2 && (
					<>
						<div className="flexbox--align-center">
							<h3>Personstatus</h3>
							<Hjelpetekst>
								Endring av personstatus er kun ment for negativ testing. Adresser og andre avhengige
								verdier vil ikke bli oppdatert for å stemme overens med ny personstatus.
							</Hjelpetekst>
						</div>
						<div className="flexbox--flex-wrap" style={{ marginTop: '10px' }}>
							<PersonstatusForm path="folkeregisterpersonstatus[0]" />
						</div>
					</>
				)}
			</div>
		</>
	)
}
