import { useNavigate } from 'react-router-dom'
import { useNaviger } from '@/utils/hooks/useNaviger'
import { Button } from '@navikt/ds-react'
import { CypressSelector } from '../../../../cypress/mocks/Selectors'
import { ArrowRightIcon } from '@navikt/aksel-icons'
import { useEffect, useState } from 'react'

type NavigerTilPersonProps = {
	ident: string
}

export const NavigerTilPerson = ({ ident }: NavigerTilPersonProps) => {
	const navigate = useNavigate()
	const [navigateIdent, setNavigateIdent] = useState<string | null>(null)
	const { loading, mutate } = useNaviger(navigateIdent)

	useEffect(() => {
		mutate().then((result) => {
			if (result?.gruppe?.id && !window.location.pathname.includes(`/${result?.gruppe?.id}`)) {
				navigate(`/gruppe/${result?.gruppe?.id}`, {
					replace: true,
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
			data-cy={CypressSelector.BUTTON_VIS_I_GRUPPE}
			variant="tertiary"
			size="xsmall"
			icon={<ArrowRightIcon />}
			loading={loading}
			onClick={handleClick}
			style={{ minWidth: '118px' }}
		>
			Vis i gruppe
		</Button>
	)
}
