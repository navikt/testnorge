import { useNavigate } from 'react-router'
import { useNaviger } from '@/utils/hooks/useNaviger'
import { Button } from '@navikt/ds-react'
import { TestComponentSelectors } from '#/mocks/Selectors'
import { ArrowRightIcon } from '@navikt/aksel-icons'
import { useEffect, useState } from 'react'
import { useCurrentBruker } from '@/utils/hooks/useBruker'

type NavigerTilPersonProps = {
	ident: string
}

export const NavigerTilPerson = ({ ident }: NavigerTilPersonProps) => {
	const navigate = useNavigate()
	const [navigateIdent, setNavigateIdent] = useState<string | null>(null)
	const { loading, mutate } = useNaviger(navigateIdent)

	const { currentBruker, loading: loadingCurrentBruker } = useCurrentBruker()
	const bankidBruker = currentBruker?.brukertype === 'BANKID'

	useEffect(() => {
		mutate().then((result) => {
			if (result?.gruppe?.id && !window.location.pathname.includes(`/${result?.gruppe?.id}`)) {
				navigate(`/gruppe/${result?.gruppe?.id}`, {
					state: {
						hovedperson: result.identHovedperson,
						visPerson: result.identNavigerTil,
						sidetall: result.sidetall,
					},
				})
			}
		})
	}, [navigateIdent])

	const handleClick = (event: any) => {
		event.stopPropagation()
		setNavigateIdent(ident)
	}

	return (
		<Button
			data-testid={TestComponentSelectors.BUTTON_VIS_I_GRUPPE}
			variant="tertiary"
			size="xsmall"
			icon={<ArrowRightIcon />}
			loading={loading}
			onClick={handleClick}
			style={{ minWidth: '118px' }}
			disabled={loadingCurrentBruker || bankidBruker}
			title={
				loadingCurrentBruker || bankidBruker ? 'Kan ikke navigere til denne gruppen' : undefined
			}
		>
			Vis i gruppe
		</Button>
	)
}
