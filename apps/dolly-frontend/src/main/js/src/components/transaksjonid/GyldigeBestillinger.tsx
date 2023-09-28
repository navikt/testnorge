import { useTransaksjonsid } from '@/utils/hooks/useTransaksjonsid'

export const erGyldig = (bestillingId: number, system: string, ident: string) => {
	const { transaksjonsid, loading } = useTransaksjonsid(system, ident, bestillingId)
	return !loading && transaksjonsid && transaksjonsid.length > 0
}
