import { BodyShort, InlineMessage } from '@navikt/ds-react'
import { useWatch } from 'react-hook-form'
import { TjenesteomraadeValues, VergemaalValues } from '@/components/fagsystem/pdlf/PdlTypes'

export const harNavTjenestevirksomhet = (
	tjenesteomraade?: Array<TjenesteomraadeValues> | null,
): boolean =>
	!!tjenesteomraade?.some(
		(omraade) =>
			omraade?.tjenestevirksomhet?.trim().toLowerCase() === 'nav' &&
			(omraade.tjenesteoppgave?.length ?? 0) > 0,
	)

export const vergemaalUtenNavTjenestevirksomhet = (
	vergemaalListe?: Array<VergemaalValues> | null,
): Array<number> =>
	(vergemaalListe ?? []).flatMap((vergemaal, idx) =>
		harNavTjenestevirksomhet(vergemaal?.tjenesteomraade) ? [] : [idx + 1],
	)

const advarselTekst =
	'Legg til et tjenesteområde der tjenestevirksomhet er «nav» og minst én tjenesteoppgave er valgt. Uten dette får ikke vergemålsregisteret kjennskap til vergen.'

interface NavTjenestevirksomhetVisningProps {
	visAdvarsel: boolean
}

const NavTjenestevirksomhetVisning = ({ visAdvarsel }: NavTjenestevirksomhetVisningProps) => (
	<div role="status" className={visAdvarsel ? 'flexbox--full-width' : undefined}>
		{visAdvarsel && (
			<InlineMessage status="warning" size="small" style={{ marginBottom: '20px' }}>
				{advarselTekst}
			</InlineMessage>
		)}
	</div>
)

interface NavTjenestevirksomhetAdvarselProps {
	tjenesteomraade?: Array<TjenesteomraadeValues> | null
}

export const NavTjenestevirksomhetAdvarsel = ({
	tjenesteomraade,
}: NavTjenestevirksomhetAdvarselProps) => (
	<NavTjenestevirksomhetVisning visAdvarsel={!harNavTjenestevirksomhet(tjenesteomraade)} />
)

interface NavTjenestevirksomhetSkjemaAdvarselProps {
	path?: string
}

export const NavTjenestevirksomhetSkjemaAdvarsel = ({
	path,
}: NavTjenestevirksomhetSkjemaAdvarselProps) => {
	const harNav = useWatch({
		name: `${path}.tjenesteomraade`,
		compute: (tjenesteomraade: Array<TjenesteomraadeValues> | null | undefined) =>
			harNavTjenestevirksomhet(tjenesteomraade),
	})

	return <NavTjenestevirksomhetVisning visAdvarsel={!harNav} />
}

interface VergemaalOppsummeringAdvarselProps {
	vergemaalListe?: Array<VergemaalValues> | null
}

export const VergemaalOppsummeringAdvarsel = ({
	vergemaalListe,
}: VergemaalOppsummeringAdvarselProps) => {
	const vergemaalUtenNav = vergemaalUtenNavTjenestevirksomhet(vergemaalListe)
	if (vergemaalUtenNav.length === 0) {
		return null
	}

	const hvilke =
		(vergemaalListe?.length ?? 0) > 1 ? `Vergemål ${vergemaalUtenNav.join(', ')}` : 'Vergemålet'

	return (
		<InlineMessage status="warning" size="small" style={{ marginBottom: '15px' }}>
			<BodyShort size="small" weight="semibold">
				{hvilke} mangler Nav som tjenestevirksomhet.
			</BodyShort>
			<BodyShort size="small">
				Gå tilbake til Vergemål under Personinformasjon. {advarselTekst}
			</BodyShort>
		</InlineMessage>
	)
}
