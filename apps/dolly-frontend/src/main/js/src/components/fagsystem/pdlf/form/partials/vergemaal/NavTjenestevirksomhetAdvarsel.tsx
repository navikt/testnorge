import { InlineMessage } from '@navikt/ds-react'
import { TjenesteomraadeValues } from '@/components/fagsystem/pdlf/PdlTypes'

export const harNavTjenestevirksomhet = (
	tjenesteomraade?: Array<TjenesteomraadeValues> | null,
): boolean =>
	!!tjenesteomraade?.some(
		(omraade) =>
			omraade?.tjenestevirksomhet?.trim().toLowerCase() === 'nav' &&
			(omraade.tjenesteoppgave?.length ?? 0) > 0,
	)

interface NavTjenestevirksomhetAdvarselProps {
	tjenesteomraade?: Array<TjenesteomraadeValues> | null
}

export const NavTjenestevirksomhetAdvarsel = ({
	tjenesteomraade,
}: NavTjenestevirksomhetAdvarselProps) => {
	const visAdvarsel = !harNavTjenestevirksomhet(tjenesteomraade)

	return (
		<div role="status" className={visAdvarsel ? 'flexbox--full-width' : undefined}>
			{visAdvarsel && (
				<InlineMessage status="warning" size="small" style={{ marginBottom: '20px' }}>
					Ingen tjenesteoppgaver er knyttet til Nav som tjenestevirksomhet. Da får ikke
					vergemålsregisteret kjennskap til vergen.
				</InlineMessage>
			)}
		</div>
	)
}
